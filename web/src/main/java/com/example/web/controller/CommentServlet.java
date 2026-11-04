
package com.example.web.controller;

import com.example.core.model.Comment;
import com.example.core.service.CommentService;
import com.example.web.dto.CommentResponse;
import com.example.web.util.ApiErrorWriter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class CommentServlet extends HttpServlet {

    private static final Logger logger =
            LoggerFactory.getLogger(CommentServlet.class);

    private CommentService commentService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        objectMapper = new ObjectMapper();

        commentService = (CommentService) getServletContext()
                .getAttribute("commentService");

        if (commentService == null) {
            throw new ServletException(
                    "CommentService is not initialized"
            );
        }

        logger.info("CommentServlet initialized");
    }

    // GET /comments/book/{bookId}
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        try {
            Long bookId = extractBookId(request);

            List<CommentResponse> result = commentService
                    .findCommentsByBookId(bookId)
                    .stream()
                    .map(CommentResponse::from)
                    .toList();

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            objectMapper.writeValue(response.getWriter(), result);

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid comment request: {}", e.getMessage());

            ApiErrorWriter.write(
                    request,
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );

        } catch (Exception e) {
            logger.error("Failed to retrieve comments", e);

            ApiErrorWriter.write(
                    request,
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal server error"
            );
        }
    }

    // POST /comments/book/{bookId}
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        try {
            Long bookId = extractBookId(request);

            JsonNode input = objectMapper.readTree(
                    request.getInputStream()
            );

            if (input == null || !input.isObject()) {
                logger.warn("Invalid JSON when creating comment");

                ApiErrorWriter.write(
                        request,
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid JSON"
                );
                return;
            }

            String author = input.path("author").asText(null);
            String text = input.path("text").asText(null);

            Comment comment = new Comment(
                    null,
                    bookId,
                    author,
                    text,
                    null
            );

            Comment savedComment = commentService.addComment(comment);

            logger.info(
                    "Comment created: commentId={}, bookId={}, author={}",
                    savedComment.getId(),
                    bookId,
                    savedComment.getAuthor()
            );

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            objectMapper.writeValue(
                    response.getWriter(),
                    CommentResponse.from(savedComment)
            );

        } catch (JsonProcessingException e) {
            logger.warn("Invalid JSON format when creating comment");

            ApiErrorWriter.write(
                    request,
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid JSON format"
            );

        } catch (IllegalArgumentException e) {
            logger.warn("Comment validation failed: {}", e.getMessage());

            ApiErrorWriter.write(
                    request,
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );

        } catch (Exception e) {
            logger.error("Failed to create comment", e);

            ApiErrorWriter.write(
                    request,
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal server error"
            );
        }
    }

    // DELETE /comments/{commentId}
    @Override
    protected void doDelete(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        try {
            Long commentId = extractCommentId(request);

            commentService.deleteComment(commentId);

            logger.info(
                    "Comment deleted: commentId={}",
                    commentId
            );

            response.setStatus(HttpServletResponse.SC_NO_CONTENT);

        } catch (IllegalArgumentException e) {
            logger.warn("Comment not found or invalid ID: {}", e.getMessage());

            ApiErrorWriter.write(
                    request,
                    response,
                    HttpServletResponse.SC_NOT_FOUND,
                    e.getMessage()
            );

        } catch (IllegalStateException e) {
            logger.warn("Comment deletion conflict: {}", e.getMessage());

            ApiErrorWriter.write(
                    request,
                    response,
                    HttpServletResponse.SC_CONFLICT,
                    e.getMessage()
            );

        } catch (Exception e) {
            logger.error("Failed to delete comment", e);

            ApiErrorWriter.write(
                    request,
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal server error"
            );
        }
    }

    // GET/POST: /comments/book/{bookId}
    private Long extractBookId(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null) {
            throw new IllegalArgumentException(
                    "Book ID is required"
            );
        }

        String[] parts = pathInfo.split("/");

        if (parts.length != 3 || !parts[1].equals("book")) {
            throw new IllegalArgumentException(
                    "Invalid comments URL"
            );
        }

        return parseId(parts[2], "Invalid book ID");
    }

    // DELETE: /comments/{commentId}
    private Long extractCommentId(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.isBlank()) {
            throw new IllegalArgumentException(
                    "Comment ID is required"
            );
        }

        String value = pathInfo.startsWith("/")
                ? pathInfo.substring(1)
                : pathInfo;

        if (value.isBlank() || value.contains("/")) {
            throw new IllegalArgumentException(
                    "Invalid comment URL"
            );
        }

        return parseId(value, "Invalid comment ID");
    }

    private Long parseId(String value, String message) {
        try {
            long id = Long.parseLong(value);

            if (id <= 0) {
                throw new IllegalArgumentException(message);
            }

            return id;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(message);
        }
    }
}
