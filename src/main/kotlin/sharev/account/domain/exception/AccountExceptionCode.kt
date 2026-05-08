package sharev.account.domain.exception

import sharev.common.domain.exception.ExceptionCategory
import sharev.common.domain.exception.ExceptionCode

enum class AccountExceptionCode(
    override val category: ExceptionCategory,
    override val message: String,
) : ExceptionCode {
    ACCOUNT_NOT_FOUND(
        ExceptionCategory.NOT_FOUND,
        "사용자가 존재하지 않습니다."
    ),
}
