package sharev.domain.member.exception;

import sharev.exception.BadRequestException;
import sharev.exception.ExceptionCode;

public class MemberAlreadyExistsException extends BadRequestException {
    public MemberAlreadyExistsException() {
        super(ExceptionCode.MEMBER_ALREADY_EXISTS);
    }
}
