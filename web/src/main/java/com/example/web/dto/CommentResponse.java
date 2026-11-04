
package com.example.web.dto;

import com.example.core.model.Comment;

public class CommentResponse {

    private Long id;
    private Long bookId;
    private String author;
    private String text;
    private String createdAt;

    public CommentResponse() {
    }

    public CommentResponse(
            Long id,
            Long bookId,
            String author,
            String text,
            String createdAt
    ) {
        this.id = id;
        this.bookId = bookId;
        this.author = author;
        this.text = text;
        this.createdAt = createdAt;
    }

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getBookId(),
                comment.getAuthor(),
                comment.getText(),
                comment.getCreatedAt() == null
                        ? null
                        : comment.getCreatedAt().toString()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getBookId() {
        return bookId;
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
