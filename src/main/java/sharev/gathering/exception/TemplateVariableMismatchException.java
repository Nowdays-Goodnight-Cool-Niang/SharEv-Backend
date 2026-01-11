package sharev.gathering.exception;

import sharev.exception.BadRequestException;
import sharev.exception.ExceptionCode;

// TODO: exceptionCode로만 설명하는 게 아니라, 에러 메시지를 직접적으로 받아 넘겨야 할 수도 있다. 즉, 전반적인 익셉션을 고쳐야 할 수 있다. 유의해둘 것.
public class TemplateVariableMismatchException extends BadRequestException {
    public TemplateVariableMismatchException() {
        super(ExceptionCode.WRONG_TEMPLATE);
    }
}
