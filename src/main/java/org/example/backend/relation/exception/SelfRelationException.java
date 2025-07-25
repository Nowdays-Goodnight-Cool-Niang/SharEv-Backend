package org.example.backend.relation.exception;

import org.example.backend.exception.BadRequestException;

public class SelfRelationException extends BadRequestException {
    public SelfRelationException() {
        super("자기 자신을 도감에 등록할 수 없습니다.");
    }
}
