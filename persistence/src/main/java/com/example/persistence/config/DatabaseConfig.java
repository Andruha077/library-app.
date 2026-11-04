package com.example.persistence.config;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {

    private final DataSource dataSource;

    public DatabaseConfig() {
        JdbcDataSource ds = new JdbcDataSource();

        ds.setURL("jdbc:h2:./data/librarydb;AUTO_SERVER=TRUE");
        ds.setUser("sa");
        ds.setPassword("");

        this.dataSource = ds;

        initializeDatabase();
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    private void initializeDatabase() {

        String booksTable = """
                CREATE TABLE IF NOT EXISTS books (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    author VARCHAR(255) NOT NULL,
                    description CLOB
                )
                """;

        String commentsTable = """
                CREATE TABLE IF NOT EXISTS comments (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    book_id BIGINT NOT NULL,
                    author VARCHAR(255) NOT NULL,
                    text CLOB NOT NULL,
                    created_at TIMESTAMP NOT NULL,
                    FOREIGN KEY (book_id) REFERENCES books(id)
                )
                """;

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute(booksTable);
            statement.execute(commentsTable);

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Database initialization failed", e
            );
        }
    }
}