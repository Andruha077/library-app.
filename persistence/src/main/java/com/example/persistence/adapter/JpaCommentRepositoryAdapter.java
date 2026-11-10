package com.example.persistence.adapter;

import com.example.core.model.Comment;
import com.example.core.port.CommentRepositoryPort;
import com.example.persistence.entity.BookEntity;
import com.example.persistence.entity.CommentEntity;
import com.example.persistence.entity.UserEntity;
import com.example.persistence.repository.BookRepository;
import com.example.persistence.repository.CommentRepository;
import com.example.persistence.repository.UserRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class JpaCommentRepositoryAdapter implements CommentRepositoryPort {

    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public JpaCommentRepositoryAdapter(
            CommentRepository commentRepository,
            BookRepository bookRepository,
            UserRepository userRepository
    ) {
        this.commentRepository = commentRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> findByBookId(Long bookId) {
        return commentRepository.findByBookId(bookId)
                .stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> findByUserId(Long userId) {
        return commentRepository.findByUserId(userId)
                .stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id)
                .map(this::toModel);
    }

    @Override
    public Comment save(Comment comment) {

        BookEntity book = bookRepository.findById(comment.getBookId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Book not found"));

        UserEntity user = userRepository.findByUsername(
                comment.getAuthor().trim()
        ).orElseThrow(() ->
                new IllegalArgumentException(
                        "User not found: " + comment.getAuthor()
                ));

        CommentEntity entity = new CommentEntity();
        entity.setBook(book);
        entity.setUser(user);
        entity.setText(comment.getText());
        entity.setCreatedAt(comment.getCreatedAt());

        return toModel(commentRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }

    private Comment toModel(CommentEntity entity) {
        return new Comment(
                entity.getId(),
                entity.getBook().getId(),
                entity.getUser().getUsername(),
                entity.getText(),
                entity.getCreatedAt()
        );
    }
}