package org.example.backend.exception;

import org.springframework.http.HttpStatus;

public abstract class CustomException extends RuntimeException {
    public final HttpStatus status;

    protected CustomException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
