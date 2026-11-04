
package com.example.web.config;

import com.example.core.config.ApplicationServicesProvider;
import com.example.core.service.BookService;
import com.example.core.service.CommentService;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.ServiceLoader;

@WebListener
public class ApplicationConfig implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {

        ApplicationServicesProvider provider =
                ServiceLoader.load(ApplicationServicesProvider.class)
                        .findFirst()
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "ApplicationServicesProvider not found"
                                )
                        );

        BookService bookService = provider.createBookService();
        CommentService commentService = provider.createCommentService();

        event.getServletContext().setAttribute(
                "bookService", bookService
        );

        event.getServletContext().setAttribute(
                "commentService", commentService
        );

        System.out.println("Library application started");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        System.out.println("Library application stopped");
    }
}
