package com.example.web.controller;

import com.example.web.LibraryInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LibraryInfoController {

    private final LibraryInfo libraryInfo;

    public LibraryInfoController(LibraryInfo libraryInfo) {
        this.libraryInfo = libraryInfo;
    }

    @GetMapping("/api/info")
    public LibraryInfo getLibraryInfo() {
        return libraryInfo;
    }
}