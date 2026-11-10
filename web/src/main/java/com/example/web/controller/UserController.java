package com.example.web.controller;

import com.example.core.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UserController {

    private final CommentService commentService;

    public UserController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/users/{id}/comments")
    public String getUserComments(
            @PathVariable("id") Long userId,
            Model model
    ) {
        model.addAttribute(
                "comments",
                commentService.findCommentsByUserId(userId)
        );

        model.addAttribute("userId", userId);

        return "user-comments";
    }
}