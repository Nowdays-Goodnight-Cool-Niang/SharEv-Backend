package sharev.card.domain.exception

import sharev.common.domain.exception.BusinessException

class CardException : BusinessException {
    constructor(code: CardExceptionCode) : super(code.toDetails())
    constructor(code: CardExceptionCode, message: String) : super(
        code.toDetails().copy(message = message)
    )
}
