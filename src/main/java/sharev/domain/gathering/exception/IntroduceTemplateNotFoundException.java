package sharev.domain.gathering.exception;

import sharev.exception.BadRequestException;
import sharev.exception.ExceptionCode;

public class IntroduceTemplateNotFoundException extends BadRequestException {
    public IntroduceTemplateNotFoundException() {
        super(ExceptionCode.INTRODUCE_TEMPLATE_NOT_FOUND);
    }
}
