package org.example.backend.exception;

public class LockOverWaitTimeException extends RuntimeException {
    public LockOverWaitTimeException() {
        super("시간이 초과되었습니다.");
    }
}
