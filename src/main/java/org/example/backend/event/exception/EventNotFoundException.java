package org.example.backend.event.exception;

import org.example.backend.exception.BadRequestException;
import org.example.backend.exception.ExceptionCode;

public class EventNotFoundException extends BadRequestException {
    public EventNotFoundException() {
        super(ExceptionCode.EVENT_NOT_FOUND);
    }
}
