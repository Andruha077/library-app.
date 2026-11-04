package com.example.core.model;

import java.time.LocalDateTime;

public class Comment {

    private Long id;
    private Long bookId;
    private String author;
    private String text;
    private LocalDateTime createdAt;

    public Comment() {
    }

    public Comment(Long id, Long bookId, String author,
                   String text, LocalDateTime createdAt) {
        this.id = id;
        this.bookId = bookId;
        this.author = author;
        this.text = text;
        this.createdAt = createdAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}