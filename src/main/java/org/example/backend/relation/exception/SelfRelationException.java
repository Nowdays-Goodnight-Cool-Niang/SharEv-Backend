package org.example.backend.relation.exception;

public class SelfRelationException extends RuntimeException {
    public SelfRelationException() {
        super("자기 자신을 도감에 등록할 수 없습니다.");
    }
}
