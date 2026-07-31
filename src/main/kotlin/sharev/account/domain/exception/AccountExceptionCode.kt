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

    ACCOUNT_SAVE_FAILED(
        ExceptionCategory.INTERNAL,
        "회원 가입 중 예상치 못 한 문제가 발생하였습니다."
    ),

    HANDLE_ALREADY_EXISTS(
        ExceptionCategory.CONFLICT,
        "이미 존재하는 handle입니다."
    )
}
