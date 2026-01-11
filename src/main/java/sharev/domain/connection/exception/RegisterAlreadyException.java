package sharev.domain.connection.exception;

import sharev.exception.BadRequestException;
import sharev.exception.ExceptionCode;

public class RegisterAlreadyException extends BadRequestException {
    public RegisterAlreadyException() {
        super(ExceptionCode.REGISTER_ALREADY);
    }
}
