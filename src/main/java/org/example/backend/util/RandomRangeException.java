package org.example.backend.util;

public class RandomRangeException extends RuntimeException {
    public RandomRangeException() {
        super("임의의 수 생성 범위가 잘못되었습니다. 관리자에게 문의하십시오.");
    }
}
