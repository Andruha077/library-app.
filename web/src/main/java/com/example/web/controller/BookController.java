
package com.example.web.controller;

import com.example.core.model.Book;
import com.example.core.pagination.Page;
import com.example.core.pagination.PageRequest;
import com.example.core.service.BookService;
import com.example.core.service.CommentService;
import com.example.web.dto.BookDetailsResponse;
import com.example.web.dto.BookResponse;
import com.example.web.dto.PageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final CommentService commentService;

    public BookController(
            BookService bookService,
            CommentService commentService
    ) {
        this.bookService = bookService;
        this.commentService = commentService;
    }

    @GetMapping
    public PageResponse<BookResponse> getBooks(
            @RequestParam(name = "q", defaultValue = "") String q,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        PageRequest pageRequest = new PageRequest(page, size, "id");
        Page<Book> result = bookService.findBooks(q, pageRequest);

        return PageResponse.from(result, BookResponse::from);
    }

    @GetMapping("/{id}")
    public BookDetailsResponse getBookById(@PathVariable("id") Long id) {
        Book book = bookService.findBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        return BookDetailsResponse.from(
                book,
                commentService.findCommentsByBookId(id)
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse createBook(@RequestBody Book book) {
        return BookResponse.from(bookService.saveBook(book));
    }
}
