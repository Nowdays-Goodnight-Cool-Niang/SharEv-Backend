package sharev.gathering.domain.exception

import sharev.common.domain.exception.ExceptionCategory
import sharev.common.domain.exception.ExceptionCode

enum class GatheringExceptionCode(
    override val category: ExceptionCategory,
    override val message: String,
) : ExceptionCode {
    GATHERING_NOT_FOUND(
        ExceptionCategory.NOT_FOUND,
        "이벤트가 존재하지 않습니다."
    ),
    INTRODUCE_TEMPLATE_NOT_FOUND(
        ExceptionCategory.NOT_FOUND,
        "자기소개 템플릿이 존재하지 않습니다."
    ),
    GATHERING_PARTICIPANT_NOT_FOUND(
        ExceptionCategory.NOT_FOUND,
        "행사 참여 정보가 존재하지 않습니다."
    ),
    WRONG_TEMPLATE(
        ExceptionCategory.BAD_REQUEST,
        "불일치하는 템플릿 content와 placeholder가 존재합니다. 다시 확인해 주세요."
    ),
}
