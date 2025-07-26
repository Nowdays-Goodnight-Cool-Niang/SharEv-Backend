package org.example.backend.exception;

import org.springframework.http.HttpStatus;

public abstract class CustomException extends RuntimeException {
    public final HttpStatus status;
    public final ExceptionCode exceptionCode;

    protected CustomException(HttpStatus status, ExceptionCode exceptionCode) {
        super(exceptionCode.message);
        this.status = status;
        this.exceptionCode = exceptionCode;
    }
}
