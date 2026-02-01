package sharev.domain.card.exception;

import sharev.exception.BadRequestException;
import sharev.exception.ExceptionCode;

public class CardUncompletedException extends BadRequestException {
    public CardUncompletedException() {
        super(ExceptionCode.CARD_UNCOMPLETED);
    }
}
