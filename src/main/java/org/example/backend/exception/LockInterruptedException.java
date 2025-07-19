package org.example.backend.exception;

public class LockInterruptedException extends RuntimeException {
    public LockInterruptedException() {
        super("예상치 못 한 오류가 발생했습니다.");
    }
}
