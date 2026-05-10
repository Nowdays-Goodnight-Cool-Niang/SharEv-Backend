package sharev.card.domain.exception

import sharev.common.domain.exception.BusinessException

class CardException : BusinessException {
    constructor(code: sharev.card.domain.exception.CardExceptionCode) : super(code.toDetails())
    constructor(code: sharev.card.domain.exception.CardExceptionCode, message: String) : super(
        code.toDetails().copy(message = message)
    )
}
