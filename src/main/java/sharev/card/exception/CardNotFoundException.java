package sharev.card.exception;

import sharev.exception.BadRequestException;
import sharev.exception.ExceptionCode;

public class CardNotFoundException extends BadRequestException {
    public CardNotFoundException() {
        super(ExceptionCode.CARD_NOT_FOUND);
    }
}
