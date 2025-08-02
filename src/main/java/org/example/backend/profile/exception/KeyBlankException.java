package org.example.backend.profile.exception;

import org.example.backend.exception.ExceptionCode;
import org.example.backend.exception.InternalServerException;

public class KeyBlankException extends InternalServerException {
    public KeyBlankException() {
        super(ExceptionCode.KEY_BLANK);
    }
}
