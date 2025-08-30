package sharev.account.exception;

import sharev.exception.BadRequestException;
import sharev.exception.ExceptionCode;

public class AccountNotFoundException extends BadRequestException {
    public AccountNotFoundException() {
        super(ExceptionCode.ACCOUNT_NOT_FOUND);
    }
}
