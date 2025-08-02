package org.example.backend.profile.exception;

import org.example.backend.exception.BadRequestException;
import org.example.backend.exception.ExceptionCode;

public class JoinAlreadyException extends BadRequestException {
    public JoinAlreadyException() {
        super(ExceptionCode.JOIN_ALREADY);
    }
}
