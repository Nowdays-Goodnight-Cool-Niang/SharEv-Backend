package org.example.backend.profile.exception;

import org.example.backend.exception.InternalServerException;

public class CheckPinNumberException extends InternalServerException {
    public CheckPinNumberException() {
        super("pin number 확인 도중 예외가 발생했습니다. 운영진에게 알려 주십시오.");
    }
}
