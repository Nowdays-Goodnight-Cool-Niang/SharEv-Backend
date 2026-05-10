package sharev.member.domain.exception

import sharev.common.domain.exception.ExceptionCategory
import sharev.common.domain.exception.ExceptionCode

enum class MemberExceptionCode(
    override val category: ExceptionCategory,
    override val message: String,
) : ExceptionCode {
    MEMBER_NOT_FOUND(
        ExceptionCategory.NOT_FOUND,
        "멤버를 찾을 수 없습니다.",
    ),
    MEMBER_ALREADY_EXISTS(
        ExceptionCategory.CONFLICT,
        "이미 팀에 존재하는 멤버입니다.",
    ),
    MEMBER_NOT_INVITED(
        ExceptionCategory.BAD_REQUEST,
        "초대 상태가 아닙니다.",
    ),
    CANNOT_REMOVE_SELF(
        ExceptionCategory.BAD_REQUEST,
        "본인을 제거할 수 없습니다. 탈퇴를 이용해주세요.",
    ),
    CANNOT_REMOVE_LAST_ADMIN(
        ExceptionCategory.BAD_REQUEST,
        "마지막 관리자는 제거할 수 없습니다.",
    ),
}
