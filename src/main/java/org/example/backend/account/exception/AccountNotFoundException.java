package org.example.backend.account.exception;

import org.example.backend.exception.BadRequestException;
import org.example.backend.exception.ExceptionCode;

public class AccountNotFoundException extends BadRequestException {
    public AccountNotFoundException() {
        super(ExceptionCode.ACCOUNT_NOT_FOUND);
    }
}
