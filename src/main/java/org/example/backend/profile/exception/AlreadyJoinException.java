package org.example.backend.profile.exception;

import org.example.backend.exception.BadRequestException;

public class AlreadyJoinException extends BadRequestException {
    public AlreadyJoinException() {
        super("이미 행사에 가입하셨습니다.");
    }
}
