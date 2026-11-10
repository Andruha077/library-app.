
package com.example.web.controller;

import com.example.core.model.Comment;
import com.example.core.service.CommentService;
import com.example.web.dto.CommentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/book/{bookId}")
    public List<CommentResponse> getCommentsByBookId(
            @PathVariable("bookId") Long bookId) {

        return commentService.findCommentsByBookId(bookId)
                .stream()
                .map(CommentResponse::from)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse createComment(@RequestBody Comment comment) {
        Comment savedComment = commentService.addComment(comment);
        return CommentResponse.from(savedComment);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable("id") Long id) {
        commentService.deleteComment(id);
    }
}
