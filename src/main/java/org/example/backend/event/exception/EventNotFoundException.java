package org.example.backend.event.exception;

import org.example.backend.exception.BadRequestException;

public class EventNotFoundException extends BadRequestException {
    public EventNotFoundException() {
        super("이벤트가 존재하지 않습니다.");
    }
}
