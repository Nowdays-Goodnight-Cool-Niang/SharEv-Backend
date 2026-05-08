package sharev.team.domain.exception

import sharev.common.domain.exception.ExceptionCategory
import sharev.common.domain.exception.ExceptionCode

enum class TeamExceptionCode(
    override val category: ExceptionCategory,
    override val message: String,
) : ExceptionCode {
    NOT_TEAM_MEMBER(
        ExceptionCategory.FORBIDDEN,
        "해당 팀의 멤버가 아닙니다."
    ),
    NOT_TEAM_ADMIN_MEMBER(
        ExceptionCategory.FORBIDDEN,
        "해당 팀의 어드민이 아닙니다."
    ),
    DUPLICATE_TEAM_NAME(
        ExceptionCategory.CONFLICT,
        "이미 존재하는 팀명입니다."
    ),
    TEAM_NOT_FOUND(
        ExceptionCategory.NOT_FOUND,
        "팀이 존재하지 않습니다."
    ),
}
