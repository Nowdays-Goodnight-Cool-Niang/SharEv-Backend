package sharev.domain.member.exception;

import sharev.exception.BadRequestException;
import sharev.exception.ExceptionCode;

public class MemberNotInvitedException extends BadRequestException {
    public MemberNotInvitedException() {
        super(ExceptionCode.MEMBER_NOT_INVITED);
    }
}
