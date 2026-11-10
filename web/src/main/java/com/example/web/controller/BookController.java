
package com.example.web.controller;

import com.example.core.model.Book;
import com.example.core.pagination.Page;
import com.example.core.pagination.PageRequest;
import com.example.core.service.BookService;
import com.example.web.service.MailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final MailService mailService;

    public BookController(
            BookService bookService,
            MailService mailService
    ) {
        this.bookService = bookService;
        this.mailService = mailService;
    }

    @GetMapping
    public String getBooks(
            @RequestParam(name = "q", defaultValue = "") String q,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model
    ) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException(
                    "Invalid pagination parameters"
            );
        }

        PageRequest pageRequest = new PageRequest(page, size, "id");
        Page<Book> result = bookService.findBooks(q, pageRequest);

        model.addAttribute("books", result.getContent());
        model.addAttribute("currentPage", result.getPage());
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("totalElements", result.getTotalElements());
        model.addAttribute("query", q);

        return "books";
    }

    @GetMapping("/add")
    public String showAddBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "book-form";
    }

    @PostMapping
    public String createBook(
            @ModelAttribute("book") Book book,
            Model model
    ) {
        try {
            bookService.saveBook(book);

            // Формування HTML-листа через FreeMarker
            mailService.sendNewBookEmail(book);

            return "redirect:/books";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "book-form";
        }
    }

    @GetMapping("/{id}")
    public String getBookById(
            @PathVariable("id") Long id,
            Model model
    ) {
        Book book = bookService.findBookById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Book not found")
                );

        model.addAttribute("book", book);

        return "book-details";
    }
}
