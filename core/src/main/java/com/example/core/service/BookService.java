
package com.example.core.service;

import com.example.core.model.Book;
import com.example.core.pagination.Page;
import com.example.core.pagination.PageRequest;
import com.example.core.port.CatalogRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookService {

    private final CatalogRepositoryPort catalogRepository;

    public BookService(CatalogRepositoryPort catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    public Page<Book> findBooks(String query, PageRequest pageRequest) {
        return catalogRepository.findAll(query, pageRequest);
    }

    public Optional<Book> findBookById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid book ID");
        }

        return catalogRepository.findById(id);
    }

    public Book saveBook(Book book) {

        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }

        if (book.getTitle() == null || book.getTitle().isBlank()) {
            throw new IllegalArgumentException("Book title is required");
        }

        if (book.getAuthor() == null || book.getAuthor().isBlank()) {
            throw new IllegalArgumentException("Book author is required");
        }

        return catalogRepository.save(book);
    }
}
