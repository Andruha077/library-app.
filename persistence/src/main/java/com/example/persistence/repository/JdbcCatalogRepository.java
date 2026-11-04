
package com.example.persistence.repository;

import com.example.core.model.Book;
import com.example.core.pagination.Page;
import com.example.core.pagination.PageRequest;
import com.example.core.port.CatalogRepositoryPort;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCatalogRepository implements CatalogRepositoryPort {

    private final DataSource dataSource;

    public JdbcCatalogRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Page<Book> findAll(String query, PageRequest pageRequest) {

        List<Book> books = new ArrayList<>();

        String searchQuery = "%" + (query == null ? "" : query) + "%";

        String sql = """
                SELECT * FROM books
                WHERE title LIKE ? OR author LIKE ?
                ORDER BY id
                LIMIT ? OFFSET ?
                """;

        String countSql = """
                SELECT COUNT(*) FROM books
                WHERE title LIKE ? OR author LIKE ?
                """;

        try (Connection connection = dataSource.getConnection()) {

            long totalElements;

            try (PreparedStatement statement =
                         connection.prepareStatement(countSql)) {

                statement.setString(1, searchQuery);
                statement.setString(2, searchQuery);

                try (ResultSet resultSet = statement.executeQuery()) {
                    resultSet.next();
                    totalElements = resultSet.getLong(1);
                }
            }

            try (PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setString(1, searchQuery);
                statement.setString(2, searchQuery);
                statement.setInt(3, pageRequest.getSize());
                statement.setInt(4,
                        pageRequest.getPage() * pageRequest.getSize());

                try (ResultSet resultSet = statement.executeQuery()) {

                    while (resultSet.next()) {
                        books.add(mapBook(resultSet));
                    }
                }
            }

            return new Page<>(
                    books,
                    pageRequest.getPage(),
                    pageRequest.getSize(),
                    totalElements
            );

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find books", e);
        }
    }

    @Override
    public Optional<Book> findById(Long id) {

        String sql = "SELECT * FROM books WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapBook(resultSet));
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find book", e);
        }
    }

    @Override
    public Book save(Book book) {

        String sql = """
                INSERT INTO books (title, author, description)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql,
                     java.sql.Statement.RETURN_GENERATED_KEYS
             )) {

            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getDescription());

            statement.executeUpdate();

            try (ResultSet resultSet = statement.getGeneratedKeys()) {

                if (resultSet.next()) {
                    book.setId(resultSet.getLong(1));
                } else {
                    throw new SQLException(
                            "Failed to retrieve generated book ID"
                    );
                }
            }

            return book;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save book", e);
        }
    }

    private Book mapBook(ResultSet resultSet) throws SQLException {

        return new Book(
                resultSet.getLong("id"),
                resultSet.getString("title"),
                resultSet.getString("author"),
                resultSet.getString("description")
        );
    }
}
