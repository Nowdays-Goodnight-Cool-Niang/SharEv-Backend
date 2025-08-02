package org.example.backend.relation.exception;

import org.example.backend.exception.BadRequestException;
import org.example.backend.exception.ExceptionCode;

public class RegisterAlreadyException extends BadRequestException {
    public RegisterAlreadyException() {
        super(ExceptionCode.REGISTER_ALREADY);
    }
}
