package org.example.backend.util;

import org.example.backend.exception.ExceptionCode;
import org.example.backend.exception.InternalServerException;

public class LockOverWaitTimeException extends InternalServerException {
    public LockOverWaitTimeException() {
        super(ExceptionCode.LOCK_OVER_WAIT_TIME);
    }
}
