package sharev.domain.account.exception;

import sharev.exception.BadRequestException;
import sharev.exception.ExceptionCode;

public class LinkNotFoundException extends BadRequestException {
    public LinkNotFoundException() {
        super(ExceptionCode.LINK_NOT_FOUND);
    }
}
