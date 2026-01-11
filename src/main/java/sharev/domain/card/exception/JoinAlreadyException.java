package sharev.domain.card.exception;

import sharev.exception.BadRequestException;
import sharev.exception.ExceptionCode;

public class JoinAlreadyException extends BadRequestException {
    public JoinAlreadyException() {
        super(ExceptionCode.JOIN_ALREADY);
    }
}
