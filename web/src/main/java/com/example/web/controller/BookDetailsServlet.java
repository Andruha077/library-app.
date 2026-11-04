package com.example.web.controller;

import com.example.core.model.Book;
import com.example.core.service.BookService;
import com.example.web.dto.BookResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/books/*")
public class BookDetailsServlet extends HttpServlet {

    private BookService bookService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        this.objectMapper = new ObjectMapper();

        this.bookService = (BookService) getServletContext()
                .getAttribute("bookService");

        if (this.bookService == null) {
            throw new ServletException("BookService is not initialized");
        }
    }

    public void setBookService(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.length() <= 1) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Book ID is required"
                );
                return;
            }

            Long bookId = Long.parseLong(pathInfo.substring(1));

            Optional<Book> optionalBook =
                    bookService.findBookById(bookId);

            if (optionalBook.isEmpty()) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Book not found"
                );
                return;
            }

            Book book = optionalBook.get();

            objectMapper.writeValue(
                    response.getWriter(),
                    BookResponse.from(book)
            );

        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid book ID"
            );
        }
    }
}