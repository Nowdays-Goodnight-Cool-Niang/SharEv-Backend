package sharev.domain.member.exception;

import sharev.exception.BadRequestException;
import sharev.exception.ExceptionCode;

public class CannotRemoveSelfException extends BadRequestException {
    public CannotRemoveSelfException() {
        super(ExceptionCode.CANNOT_REMOVE_SELF);
    }
}
