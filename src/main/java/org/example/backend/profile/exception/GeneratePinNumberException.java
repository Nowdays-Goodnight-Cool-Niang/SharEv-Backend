package org.example.backend.profile.exception;

import org.example.backend.exception.BadRequestException;
import org.example.backend.exception.InternalServerException;

public class GeneratePinNumberException extends InternalServerException {
    public GeneratePinNumberException() {
        super("pin number 발급 도중 예외가 발생했습니다. 운영진에게 알려 주십시오.");
    }
}
