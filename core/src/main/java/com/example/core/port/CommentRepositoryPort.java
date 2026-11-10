
package com.example.core.port;

import com.example.core.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepositoryPort {

    List<Comment> findByBookId(Long bookId);

    List<Comment> findByUserId(Long userId);

    Optional<Comment> findById(Long id);

    Comment save(Comment comment);

    void deleteById(Long id);
}
