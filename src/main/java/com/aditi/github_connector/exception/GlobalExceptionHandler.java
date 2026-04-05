package com.aditi.github_connector.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {

        String message = ex.getMessage() != null ? ex.getMessage() : "";

        try {
            String[] parts = message.split(":", 2);
            int statusCode = Integer.parseInt(parts[0]);
            String body = parts[1];

            return ResponseEntity
                    .status(statusCode)
                    .body(body);
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(message);
        }
    }
}