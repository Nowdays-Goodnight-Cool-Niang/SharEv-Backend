package sharev.link.domain.exception

import sharev.common.domain.exception.ExceptionCategory
import sharev.common.domain.exception.ExceptionCode
import sharev.link.domain.model.Link

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
    ),
    LINK_LIMIT_EXCEEDED(
        ExceptionCategory.BAD_REQUEST,
        "링크는 최대 ${Link.MAX_COUNT}개까지 등록할 수 있습니다."
    )
}
