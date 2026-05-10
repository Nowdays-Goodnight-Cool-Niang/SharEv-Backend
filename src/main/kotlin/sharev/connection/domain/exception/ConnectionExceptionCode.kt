package sharev.connection.domain.exception

import sharev.common.domain.exception.ExceptionCategory
import sharev.common.domain.exception.ExceptionCode

enum class ConnectionExceptionCode(
    override val category: ExceptionCategory,
    override val message: String,
) : ExceptionCode {
    REGISTER_ALREADY(
        ExceptionCategory.CONFLICT,
        "이미 도감에 등록된 명함입니다."
    ),
    REGISTER_MYSELF(
        ExceptionCategory.BAD_REQUEST,
        "자기 자신을 도감에 등록할 수 없습니다."
    ),
}
