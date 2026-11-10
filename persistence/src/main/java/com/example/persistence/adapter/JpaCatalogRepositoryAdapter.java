
package com.example.persistence.adapter;

import com.example.core.model.Book;
import com.example.core.pagination.Page;
import com.example.core.pagination.PageRequest;
import com.example.core.port.CatalogRepositoryPort;
import com.example.persistence.entity.BookEntity;
import com.example.persistence.repository.BookRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class JpaCatalogRepositoryAdapter implements CatalogRepositoryPort {

    private final BookRepository bookRepository;

    public JpaCatalogRepositoryAdapter(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> findAll(String query, PageRequest pageRequest) {

        List<BookEntity> entities = bookRepository.findAll(
                Sort.by("id").ascending()
        );

        String search = query == null ? "" : query.trim().toLowerCase();

        List<Book> filtered = entities.stream()
                .filter(book ->
                        search.isEmpty()
                                || book.getTitle().toLowerCase().contains(search)
                                || book.getAuthor().toLowerCase().contains(search)
                )
                .map(this::toModel)
                .toList();

        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        int fromIndex = Math.min(page * size, filtered.size());
        int toIndex = Math.min(fromIndex + size, filtered.size());

        return new Page<>(
                filtered.subList(fromIndex, toIndex),
                page,
                size,
                filtered.size()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id).map(this::toModel);
    }

    @Override
    public Book save(Book book) {

        BookEntity entity = new BookEntity();

        entity.setId(book.getId());
        entity.setTitle(book.getTitle());
        entity.setAuthor(book.getAuthor());
        entity.setDescription(book.getDescription());
        entity.setYear(book.getYear());

        return toModel(bookRepository.save(entity));
    }

    private Book toModel(BookEntity entity) {
        return new Book(
                entity.getId(),
                entity.getTitle(),
                entity.getAuthor(),
                entity.getDescription(),
                entity.getYear()
        );
    }
}
