package sharev.card.exception;

import sharev.exception.BadRequestException;
import sharev.exception.ExceptionCode;

public class InvalidIntroduceTemplateException extends BadRequestException {
    public InvalidIntroduceTemplateException() {
        super(ExceptionCode.INVALID_INTRODUCE_TEMPLATE);
    }
}
