package org.example.backend.exception;

import org.springframework.http.HttpStatus;

public abstract class BadRequestException extends CustomException {
    protected BadRequestException(ExceptionCode exceptionCode) {
        super(HttpStatus.BAD_REQUEST, exceptionCode);
    }
}
