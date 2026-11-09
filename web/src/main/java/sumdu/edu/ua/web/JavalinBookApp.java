
package sumdu.edu.ua.web;

import com.example.core.config.ApplicationServicesProvider;
import com.example.core.model.Book;
import com.example.core.model.Comment;
import com.example.core.pagination.PageRequest;
import com.example.core.service.BookService;
import com.example.core.service.CommentService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.javalin.Javalin;
import io.javalin.json.JsonMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.ServiceLoader;

public class JavalinBookApp {

    private static final Logger log =
            LoggerFactory.getLogger(JavalinBookApp.class);

    public static void main(String[] args) {

        System.out.println("Working directory: "
                + System.getProperty("user.dir"));

        System.out.println("Database path: "
                + System.getProperty("user.dir")
                + "/data/librarydb.mv.db");

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

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Javalin app = Javalin.create(config ->
                config.jsonMapper(new JsonMapper() {

                    @Override
                    public String toJsonString(Object obj, Type type) {
                        try {
                            return objectMapper.writeValueAsString(obj);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public <T> T fromJsonString(String json, Type type) {
                        try {
                            return objectMapper.readValue(
                                    json,
                                    objectMapper.constructType(type)
                            );
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
        );

        // Логування HTTP-запитів
        app.before(ctx ->
                log.info("{} {}", ctx.method(), ctx.path())
        );

        // Отримання списку книг
        app.get("/books", ctx -> {
            String query = ctx.queryParam("q");
            int page = parseInt(ctx.queryParam("page"), 0);
            int size = parseInt(ctx.queryParam("size"), 10);
            String sort = ctx.queryParam("sort");

            PageRequest pageRequest =
                    new PageRequest(page, size, sort);

            ctx.json(bookService.findBooks(query, pageRequest));
        });

        // Отримання книги за ID
        app.get("/books/{id}", ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));

            Book book = bookService.findBookById(id)
                    .orElseThrow(() ->
                            new BookNotFoundException("Book not found")
                    );

            ctx.json(book);
        });

        // Додавання книги
        app.post("/books", ctx -> {
            Book book = ctx.bodyAsClass(Book.class);

            Book savedBook = bookService.saveBook(book);

            ctx.status(201).json(savedBook);
        });

        // Отримання коментарів книги
        app.get("/comments/book/{bookId}", ctx -> {
            Long bookId = Long.parseLong(ctx.pathParam("bookId"));

            ctx.json(commentService.findCommentsByBookId(bookId));
        });

        // Додавання коментаря
        app.post("/comments", ctx -> {
            Comment comment = ctx.bodyAsClass(Comment.class);

            Comment savedComment =
                    commentService.addComment(comment);

            ctx.status(201).json(savedComment);
        });

        // Видалення коментаря
        app.delete("/comments/{id}", ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));

            commentService.deleteComment(id);

            ctx.status(204);
        });

        // Обробка помилок 400 і 404
        app.exception(IllegalArgumentException.class, (e, ctx) -> {
            if ("Comment not found".equals(e.getMessage())) {
                ctx.status(404).json(
                        Map.of("error", e.getMessage())
                );
            } else {
                ctx.status(400).json(
                        Map.of("error", e.getMessage())
                );
            }
        });

        // Обробка помилки 404 для книги
        app.exception(BookNotFoundException.class, (e, ctx) ->
                ctx.status(404).json(
                        Map.of("error", e.getMessage())
                )
        );

        // Обробка помилок 409
        app.exception(IllegalStateException.class, (e, ctx) ->
                ctx.status(409).json(
                        Map.of("error", e.getMessage())
                )
        );

        // Загальна обробка помилок 500
        app.exception(Exception.class, (e, ctx) -> {
            log.error("Unexpected server error", e);

            ctx.status(500).json(
                    Map.of("error", "Internal server error")
            );
        });

        app.start(7070);

        log.info("Javalin application started on port 7070");
    }

    private static int parseInt(String value, int defaultValue) {
        return value == null
                ? defaultValue
                : Integer.parseInt(value);
    }

    private static class BookNotFoundException
            extends RuntimeException {

        public BookNotFoundException(String message) {
            super(message);
        }
    }
}
