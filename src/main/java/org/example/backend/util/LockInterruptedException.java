package org.example.backend.util;

import org.example.backend.exception.ExceptionCode;
import org.example.backend.exception.InternalServerException;

public class LockInterruptedException extends InternalServerException {
    public LockInterruptedException() {
        super(ExceptionCode.LOCK_INTERRUPTED);
    }
}
