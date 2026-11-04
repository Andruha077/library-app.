
package com.example.web.controller;

import com.example.core.model.Book;
import com.example.core.pagination.Page;
import com.example.core.pagination.PageRequest;
import com.example.core.service.BookService;
import com.example.web.dto.BookResponse;
import com.example.web.dto.PageResponse;
import com.example.web.util.ApiErrorWriter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/books")
public class BookServlet extends HttpServlet {

    private static final Logger logger =
            LoggerFactory.getLogger(BookServlet.class);

    private BookService bookService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {

        logger.info("BookServlet initialization started");

        this.objectMapper = new ObjectMapper();

        this.bookService = (BookService) getServletContext()
                .getAttribute("bookService");

        if (this.bookService == null) {
            logger.error("BookService is not initialized");

            throw new ServletException(
                    "BookService is not initialized"
            );
        }

        logger.info("BookServlet initialized successfully");
    }

    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    // GET /books?q=&page=&size=&sort=
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        try {
            String query = request.getParameter("q");

            int page = parseInteger(
                    request.getParameter("page"), 0
            );

            int size = parseInteger(
                    request.getParameter("size"), 10
            );

            String sort = request.getParameter("sort");

            PageRequest pageRequest =
                    new PageRequest(page, size, sort);

            Page<Book> books =
                    bookService.findBooks(query, pageRequest);

            PageResponse<BookResponse> result =
                    PageResponse.from(books, BookResponse::from);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            objectMapper.writeValue(
                    response.getWriter(),
                    result
            );

        } catch (IllegalArgumentException e) {
            logger.warn(
                    "Invalid books request: {}",
                    e.getMessage()
            );

            ApiErrorWriter.write(
                    request,
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );

        } catch (Exception e) {
            logger.error("Failed to retrieve books", e);

            ApiErrorWriter.write(
                    request,
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal server error"
            );
        }
    }

    // POST /books
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        try {
            Book book = objectMapper.readValue(
                    request.getInputStream(),
                    Book.class
            );

            Book savedBook = bookService.saveBook(book);

            logger.info(
                    "Book created successfully: bookId={}",
                    savedBook.getId()
            );

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            objectMapper.writeValue(
                    response.getWriter(),
                    BookResponse.from(savedBook)
            );

        } catch (JsonProcessingException e) {
            logger.warn("Invalid JSON when creating book");

            ApiErrorWriter.write(
                    request,
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid JSON format"
            );

        } catch (IllegalArgumentException e) {
            logger.warn(
                    "Book validation failed: {}",
                    e.getMessage()
            );

            ApiErrorWriter.write(
                    request,
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );

        } catch (Exception e) {
            logger.error("Failed to create book", e);

            ApiErrorWriter.write(
                    request,
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal server error"
            );
        }
    }

    private int parseInteger(String value, int defaultValue) {

        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Invalid numeric parameter: " + value
            );
        }
    }
}
