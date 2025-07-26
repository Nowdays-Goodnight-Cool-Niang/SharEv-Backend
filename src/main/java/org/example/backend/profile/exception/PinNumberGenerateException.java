package org.example.backend.profile.exception;

import org.example.backend.exception.ExceptionCode;
import org.example.backend.exception.InternalServerException;

public class PinNumberGenerateException extends InternalServerException {
    public PinNumberGenerateException() {
        super(ExceptionCode.PIN_NUMBER_GENERATE);
    }
}
