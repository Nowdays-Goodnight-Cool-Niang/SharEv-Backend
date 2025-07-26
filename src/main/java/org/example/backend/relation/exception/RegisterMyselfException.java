package org.example.backend.relation.exception;

import org.example.backend.exception.BadRequestException;
import org.example.backend.exception.ExceptionCode;

public class RegisterMyselfException extends BadRequestException {
    public RegisterMyselfException() {
        super(ExceptionCode.REGISTER_MYSELF);
    }
}
