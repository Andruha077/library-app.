package com.example.core.port;

import com.example.core.model.Book;
import com.example.core.pagination.Page;
import com.example.core.pagination.PageRequest;

import java.util.Optional;

public interface CatalogRepositoryPort {

    Page<Book> findAll(String query, PageRequest pageRequest);

    Optional<Book> findById(Long id);

    Book save(Book book);
}