package sharev.card.exception;

import sharev.exception.ExceptionCode;
import sharev.exception.InternalServerException;

public class PinNumberGenerateException extends InternalServerException {
    public PinNumberGenerateException() {
        super(ExceptionCode.PIN_NUMBER_GENERATE);
    }
}
