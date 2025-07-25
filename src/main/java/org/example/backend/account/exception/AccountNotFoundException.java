package org.example.backend.account.exception;

import org.example.backend.exception.BadRequestException;

public class AccountNotFoundException extends BadRequestException {
    public AccountNotFoundException() {
        super("유저가 존재하지 않습니다.");
    }
}
