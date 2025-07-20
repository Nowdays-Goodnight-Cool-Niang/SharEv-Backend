package org.example.backend.util;

import org.example.backend.exception.InternalServerException;

public class LockOverWaitTimeException extends InternalServerException {
    public LockOverWaitTimeException() {
        super("시간이 초과되었습니다.");
    }
}
