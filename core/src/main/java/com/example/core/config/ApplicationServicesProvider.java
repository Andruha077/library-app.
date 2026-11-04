
package com.example.core.config;

import com.example.core.service.BookService;
import com.example.core.service.CommentService;

public interface ApplicationServicesProvider {

    BookService createBookService();

    CommentService createCommentService();
}
