package sharev.domain.member.exception;

import sharev.exception.BadRequestException;
import sharev.exception.ExceptionCode;

public class CannotRemoveLastAdminException extends BadRequestException {
    public CannotRemoveLastAdminException() {
        super(ExceptionCode.CANNOT_REMOVE_LAST_ADMIN);
    }
}
