package com.example.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LibraryConfig {

    @Bean
    public LibraryInfo libraryInfo(
            @Value("${app.library.name}") String name,
            @Value("${app.library.version}") String version) {

        return new LibraryInfo(name, version);
    }
}