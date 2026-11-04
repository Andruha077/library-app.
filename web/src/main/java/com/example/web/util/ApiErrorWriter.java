
package com.example.web.util;

import com.example.web.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public final class ApiErrorWriter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ApiErrorWriter() {
    }

    public static void write(
            HttpServletRequest request,
            HttpServletResponse response,
            int status,
            String message
    ) throws IOException {

        String error = switch (status) {
            case 400 -> "Bad Request";
            case 404 -> "Not Found";
            case 409 -> "Conflict";
            case 500 -> "Internal Server Error";
            default -> "Error";
        };

        ErrorResponse errorResponse = new ErrorResponse(
                status,
                error,
                message,
                request.getRequestURI()
        );

        response.reset();
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        OBJECT_MAPPER.writeValue(
                response.getWriter(),
                errorResponse
        );
    }
}
