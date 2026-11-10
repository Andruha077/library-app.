package com.example.web.service;

import com.example.core.model.Book;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailService {

    private final EmailTemplateProcessor templateProcessor;

    private String lastGeneratedEmail;

    public MailService(EmailTemplateProcessor templateProcessor) {
        this.templateProcessor = templateProcessor;
    }

    public String sendNewBookEmail(Book book) {

        Map<String, Object> model = new HashMap<>();

        model.put("title", book.getTitle());
        model.put("author", book.getAuthor());
        model.put("year", book.getYear());
        String formattedDate = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        );

        model.put("added", formattedDate);

        lastGeneratedEmail = templateProcessor.process(
                "new_book.ftl",
                model
        );

        System.out.println("HTML email generated successfully.");

        return lastGeneratedEmail;
    }

    public String getLastGeneratedEmail() {
        return lastGeneratedEmail;
    }
}
