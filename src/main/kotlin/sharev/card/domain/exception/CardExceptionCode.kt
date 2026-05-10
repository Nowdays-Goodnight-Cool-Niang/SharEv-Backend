package sharev.card.domain.exception

import sharev.common.domain.exception.ExceptionCategory
import sharev.common.domain.exception.ExceptionCode

enum class CardExceptionCode(
    override val category: ExceptionCategory,
    override val message: String,
) : ExceptionCode {
    JOIN_ALREADY(
        ExceptionCategory.CONFLICT,
        "이미 행사에 가입하셨습니다."
    ),
    CARD_NOT_FOUND(
        ExceptionCategory.NOT_FOUND,
        "명함이 존재하지 않습니다."
    ),
    CARD_UNCOMPLETED(
        ExceptionCategory.BAD_REQUEST,
        "명함이 완성되지 않았습니다."
    ),
    INVALID_INTRODUCE_TEMPLATE(
        ExceptionCategory.BAD_REQUEST,
        "작성된 소개문이 템플릿과 일치하지 않습니다."
    ),
    KEY_ERROR(
        ExceptionCategory.BAD_REQUEST,
        "pin number 확인 도중 예외가 발생했습니다. 운영진에게 알려 주십시오."
    ),
    PIN_NUMBER_GENERATE(
        ExceptionCategory.BAD_REQUEST,
        "pin number 발급 도중 예외가 발생했습니다. 운영진에게 알려 주십시오."
    ),
}
