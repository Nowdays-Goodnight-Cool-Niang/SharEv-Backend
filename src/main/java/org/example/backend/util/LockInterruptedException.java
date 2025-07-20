package org.example.backend.util;

import org.example.backend.exception.InternalServerException;

public class LockInterruptedException extends InternalServerException {
    public LockInterruptedException() {
        super("예상치 못 한 오류가 발생했습니다. 운영진에게 문의해주세요.");
    }
}
