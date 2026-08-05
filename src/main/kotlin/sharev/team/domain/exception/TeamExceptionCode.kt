package sharev.team.domain.exception

import sharev.common.domain.exception.ExceptionCategory
import sharev.common.domain.exception.ExceptionCode

enum class TeamExceptionCode(
    override val category: ExceptionCategory,
    override val message: String,
) : ExceptionCode {
    UNAUTHORIZED_TEAM_ACCESS(
        ExceptionCategory.FORBIDDEN,
        "해당 팀에 대한 접근 권한이 없습니다."
    ),
    UNAUTHORIZED_TEAM_MANAGE(
        ExceptionCategory.FORBIDDEN,
        "해당 팀에 대한 관리 권한이 없습니다."
    ),
    DUPLICATE_TEAM_NAME(
        ExceptionCategory.CONFLICT,
        "이미 존재하는 팀명입니다."
    ),
    TEAM_NOT_FOUND(
        ExceptionCategory.NOT_FOUND,
        "팀이 존재하지 않습니다."
    ),
    NOT_MODIFIABLE_TEAM(
        ExceptionCategory.FORBIDDEN,
        "수정 가능한 팀 형태가 아닙니다."
    ),
    TEAM_INFO_REQUIRED(
        ExceptionCategory.BAD_REQUEST,
        "제목과 내용은 비워둘 수 없습니다."
    ),
}
