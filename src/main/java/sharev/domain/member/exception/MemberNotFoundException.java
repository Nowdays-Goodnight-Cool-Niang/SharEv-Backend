package sharev.domain.member.exception;

import sharev.exception.BadRequestException;
import sharev.exception.ExceptionCode;

public class MemberNotFoundException extends BadRequestException {
    public MemberNotFoundException() {
        super(ExceptionCode.MEMBER_NOT_FOUND);
    }
}
