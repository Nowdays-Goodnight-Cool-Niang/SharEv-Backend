package org.example.backend.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class UltimatumExceptionsAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeExceptions(HttpServletRequest request, RuntimeException e) {
        return ResponseEntity
            .badRequest()
            .body(BasicErrorResponse.of(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleExceptions(HttpServletRequest request, Exception e) {
        return ResponseEntity
            .badRequest()
            .body(BasicErrorResponse.of(e.getMessage()));
    }

    private static class BasicErrorResponse {
        private boolean success;
        private String message;

        static BasicErrorResponse of(String message) {
            BasicErrorResponse response = new BasicErrorResponse();
            response.success = false;
            response.message = message;
            return response;
        }
    }
}
