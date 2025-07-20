package org.example.backend.exception;

import org.springframework.http.HttpStatus;

public abstract class BadRequestException extends CustomException {
    protected BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
