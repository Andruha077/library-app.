
package com.example.web.dto;

import com.example.core.model.Book;
import com.example.core.model.Comment;

import java.util.List;

public class BookDetailsResponse {

    private BookResponse book;
    private List<CommentResponse> comments;

    public BookDetailsResponse() {
    }

    public BookDetailsResponse(BookResponse book, List<CommentResponse> comments) {
        this.book = book;
        this.comments = comments;
    }

    public static BookDetailsResponse from(Book book, List<Comment> comments) {
        return new BookDetailsResponse(
                BookResponse.from(book),
                comments.stream()
                        .map(CommentResponse::from)
                        .toList()
        );
    }

    public BookResponse getBook() {
        return book;
    }

    public List<CommentResponse> getComments() {
        return comments;
    }
}
