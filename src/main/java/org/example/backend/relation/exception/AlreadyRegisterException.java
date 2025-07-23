package org.example.backend.relation.exception;

import org.example.backend.exception.BadRequestException;

public class AlreadyRegisterException extends BadRequestException {
    public AlreadyRegisterException() {
        super("이미 도감에 등록된 프로필입니다.");
    }
}
