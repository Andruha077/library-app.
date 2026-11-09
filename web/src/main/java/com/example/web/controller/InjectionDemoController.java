package com.example.web.controller;

import com.example.core.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InjectionDemoController {

    @Autowired
    private BookService bookService;

    @GetMapping("/api/injection-test")
    public String testFieldInjection() {

        if (bookService != null) {
            return "Field Injection works! BookService bean was injected.";
        }

        return "Field Injection failed!";
    }
}
