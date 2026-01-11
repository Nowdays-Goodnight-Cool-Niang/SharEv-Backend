package sharev.domain.card.exception;

import sharev.exception.ExceptionCode;
import sharev.exception.InternalServerException;

public class KeyBlankException extends InternalServerException {
    public KeyBlankException() {
        super(ExceptionCode.KEY_BLANK);
    }
}
