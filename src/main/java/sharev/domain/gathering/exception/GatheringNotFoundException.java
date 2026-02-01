package sharev.domain.gathering.exception;

import sharev.exception.BadRequestException;
import sharev.exception.ExceptionCode;

public class GatheringNotFoundException extends BadRequestException {
    public GatheringNotFoundException() {
        super(ExceptionCode.EVENT_NOT_FOUND);
    }
}
