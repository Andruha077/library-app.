package com.example.persistence.config;

import com.example.core.config.ApplicationServicesProvider;
import com.example.core.port.CatalogRepositoryPort;
import com.example.core.port.CommentRepositoryPort;
import com.example.core.service.BookService;
import com.example.core.service.CommentService;
import com.example.persistence.repository.JdbcCatalogRepository;
import com.example.persistence.repository.JdbcCommentRepository;

public class JdbcApplicationServicesProvider
        implements ApplicationServicesProvider {

    private final DatabaseConfig databaseConfig;

    public JdbcApplicationServicesProvider() {
        this.databaseConfig = new DatabaseConfig();
    }

    @Override
    public BookService createBookService() {
        CatalogRepositoryPort repository =
                new JdbcCatalogRepository(databaseConfig.getDataSource());

        return new BookService(repository);
    }

    @Override
    public CommentService createCommentService() {
        CommentRepositoryPort repository =
                new JdbcCommentRepository(databaseConfig.getDataSource());

        return new CommentService(repository);
    }
}
