
package com.example.persistence.repository;

import com.example.core.model.Comment;
import com.example.core.port.CommentRepositoryPort;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcCommentRepository implements CommentRepositoryPort {

    private final DataSource dataSource;

    public JdbcCommentRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Comment> findByBookId(Long bookId) {

        String sql = """
                SELECT * FROM comments
                WHERE book_id = ?
                ORDER BY created_at DESC
                """;

        List<Comment> comments = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, bookId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    comments.add(mapComment(rs));
                }
            }

            return comments;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load comments", e);
        }
    }

    @Override
    public Optional<Comment> findById(Long id) {

        String sql = "SELECT * FROM comments WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapComment(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find comment", e);
        }
    }

    @Override
    public Comment save(Comment comment) {

        String sql = """
                INSERT INTO comments (book_id, author, text, created_at)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, comment.getBookId());
            statement.setString(2, comment.getAuthor());
            statement.setString(3, comment.getText());
            statement.setTimestamp(
                    4,
                    Timestamp.valueOf(comment.getCreatedAt())
            );

            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Comment(
                            keys.getLong(1),
                            comment.getBookId(),
                            comment.getAuthor(),
                            comment.getText(),
                            comment.getCreatedAt()
                    );
                }
            }

            throw new SQLException("Failed to obtain generated comment ID");

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save comment", e);
        }
    }

    @Override
    public void deleteById(Long id) {

        String sql = "DELETE FROM comments WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new IllegalArgumentException("Comment not found");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete comment", e);
        }
    }

    private Comment mapComment(ResultSet rs) throws SQLException {

        Timestamp timestamp = rs.getTimestamp("created_at");

        return new Comment(
                rs.getLong("id"),
                rs.getLong("book_id"),
                rs.getString("author"),
                rs.getString("text"),
                timestamp != null ? timestamp.toLocalDateTime() : null
        );
    }
}
