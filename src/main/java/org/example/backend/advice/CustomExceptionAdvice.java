package org.example.backend.advice;

import org.example.backend.exception.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionAdvice {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<String> handleBindException(CustomException exception) {

        return ResponseEntity.status(exception.status)
                .body(exception.getMessage());
    }
}
