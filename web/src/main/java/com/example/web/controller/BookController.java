
package com.example.web.controller;

import com.example.core.model.Book;
import com.example.core.pagination.Page;
import com.example.core.pagination.PageRequest;
import com.example.core.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public Page<Book> getBooks(
            @RequestParam(name = "q", defaultValue = "") String q,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        PageRequest pageRequest = new PageRequest(page, size, "id");

        return bookService.findBooks(q, pageRequest);
    }

    @GetMapping("/{id}")
    public Book getBookById(@PathVariable("id") Long id) {
        return bookService.findBookById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Book not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book createBook(@RequestBody Book book) {
        return bookService.saveBook(book);
    }
}
