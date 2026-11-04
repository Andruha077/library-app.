
package com.example.web.controller;

import com.example.core.model.Book;
import com.example.core.service.BookService;
import com.example.web.dto.BookResponse;
import com.example.web.util.ApiErrorWriter;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/books/*")
public class BookByIdServlet extends HttpServlet {

    private static final Logger logger =
            LoggerFactory.getLogger(BookByIdServlet.class);

    private BookService bookService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {

        logger.info("BookByIdServlet initialization started");

        this.objectMapper = new ObjectMapper();

        this.bookService = (BookService) getServletContext()
                .getAttribute("bookService");

        if (this.bookService == null) {
            logger.error("BookService is not initialized");

            throw new ServletException(
                    "BookService is not initialized"
            );
        }

        logger.info("BookByIdServlet initialized successfully");
    }

    // GET /books/{id}
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")
                    || pathInfo.substring(1).contains("/")) {

                logger.warn(
                        "Invalid book URL: {}",
                        request.getRequestURI()
                );

                ApiErrorWriter.write(
                        request,
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Book ID is required or URL is invalid"
                );
                return;
            }

            Long id = Long.parseLong(pathInfo.substring(1));

            if (id <= 0) {
                logger.warn("Invalid book ID: {}", id);

                ApiErrorWriter.write(
                        request,
                        response,
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid book ID"
                );
                return;
            }

            Optional<Book> book = bookService.findBookById(id);

            if (book.isEmpty()) {
                logger.warn("Book not found: bookId={}", id);

                ApiErrorWriter.write(
                        request,
                        response,
                        HttpServletResponse.SC_NOT_FOUND,
                        "Book not found"
                );
                return;
            }

            logger.info("Book retrieved successfully: bookId={}", id);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            objectMapper.writeValue(
                    response.getWriter(),
                    BookResponse.from(book.get())
            );

        } catch (NumberFormatException e) {
            logger.warn(
                    "Invalid book ID format: {}",
                    request.getPathInfo()
            );

            ApiErrorWriter.write(
                    request,
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid book ID"
            );

        } catch (Exception e) {
            logger.error(
                    "Failed to retrieve book: {}",
                    request.getRequestURI(),
                    e
            );

            ApiErrorWriter.write(
                    request,
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal server error"
            );
        }
    }
}
