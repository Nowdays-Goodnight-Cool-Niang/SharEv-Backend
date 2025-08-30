package sharev.event.exception;

import sharev.exception.BadRequestException;
import sharev.exception.ExceptionCode;

public class EventNotFoundException extends BadRequestException {
    public EventNotFoundException() {
        super(ExceptionCode.EVENT_NOT_FOUND);
    }
}
