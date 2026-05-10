package sharev.link.domain.exception

import sharev.common.domain.exception.ExceptionCategory
import sharev.common.domain.exception.ExceptionCode

enum class LinkExceptionCode(
    override val category: ExceptionCategory,
    override val message: String,
) : ExceptionCode {
    LINK_NOT_FOUND(
        ExceptionCategory.NOT_FOUND,
        "링크가 존재하지 않습니다."
    ),
    LINK_OWNERSHIP_MISMATCH(
        ExceptionCategory.FORBIDDEN,
        "링크의 소유자가 아닙니다."
    )
}
