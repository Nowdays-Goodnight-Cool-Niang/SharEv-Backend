package org.example.backend.exception;

import org.springframework.http.HttpStatus;

public abstract class InternalServerException extends CustomException {
    protected InternalServerException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
