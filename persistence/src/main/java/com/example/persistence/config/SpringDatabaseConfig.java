
package com.example.persistence.config;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class SpringDatabaseConfig {

    @Bean
    public DataSource dataSource(
            @Value("${app.database.url:jdbc:h2:./data/librarydb;AUTO_SERVER=TRUE}") String url,
            @Value("${app.database.username:sa}") String username,
            @Value("${app.database.password:}") String password) {

        JdbcDataSource dataSource = new JdbcDataSource();

        dataSource.setURL(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);

        initializeDatabase(dataSource);

        return dataSource;
    }

    private void initializeDatabase(DataSource dataSource) {

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS books (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        title VARCHAR(255) NOT NULL,
                        author VARCHAR(255) NOT NULL,
                        description TEXT
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS comments (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        book_id BIGINT NOT NULL,
                        author VARCHAR(255) NOT NULL,
                        text TEXT NOT NULL,
                        created_at TIMESTAMP NOT NULL,
                        FOREIGN KEY (book_id) REFERENCES books(id)
                    )
                    """);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
}
