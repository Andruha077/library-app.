package com.example.core.service;

import com.example.core.model.Comment;
import com.example.core.port.CommentRepositoryPort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class CommentService {

    private final CommentRepositoryPort commentRepository;

    public CommentService(CommentRepositoryPort commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> findCommentsByBookId(Long bookId) {
        return commentRepository.findByBookId(bookId);
    }

    public Comment addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("Comment cannot be null");
        }

        if (comment.getBookId() == null || comment.getBookId() <= 0) {
            throw new IllegalArgumentException("Invalid book ID");
        }

        if (comment.getAuthor() == null || comment.getAuthor().isBlank()) {
            throw new IllegalArgumentException("Author cannot be empty");
        }

        if (comment.getText() == null || comment.getText().isBlank()) {
            throw new IllegalArgumentException("Comment text cannot be empty");
        }

        if (comment.getText().length() > 2000) {
            throw new IllegalArgumentException("Comment is too long");
        }

        Comment newComment = new Comment(
                null,
                comment.getBookId(),
                comment.getAuthor().trim(),
                comment.getText().trim(),
                LocalDateTime.now()
        );

        return commentRepository.save(newComment);
    }

    public void deleteComment(Long commentId) {
        if (commentId == null || commentId <= 0) {
            throw new IllegalArgumentException("Invalid comment ID");
        }

        Optional<Comment> optionalComment =
                commentRepository.findById(commentId);

        if (optionalComment.isEmpty()) {
            throw new IllegalArgumentException("Comment not found");
        }

        Comment comment = optionalComment.get();

        if (comment.getCreatedAt() == null) {
            throw new IllegalStateException("Comment creation date is missing");
        }

        LocalDateTime expirationTime = comment.getCreatedAt().plusHours(24);

        if (LocalDateTime.now().isAfter(expirationTime)) {
            throw new IllegalStateException(
                    "Comment can only be deleted within 24 hours"
            );
        }

        commentRepository.deleteById(commentId);
    }
}