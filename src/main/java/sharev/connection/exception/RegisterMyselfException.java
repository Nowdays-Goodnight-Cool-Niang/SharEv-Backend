package sharev.connection.exception;

import sharev.exception.BadRequestException;
import sharev.exception.ExceptionCode;

public class RegisterMyselfException extends BadRequestException {
    public RegisterMyselfException() {
        super(ExceptionCode.REGISTER_MYSELF);
    }
}
