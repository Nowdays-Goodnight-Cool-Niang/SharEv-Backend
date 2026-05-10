package sharev.member.domain.exception

import sharev.common.domain.exception.BusinessException

class MemberException : BusinessException {
    constructor(code: MemberExceptionCode) : super(code.toDetails())
    constructor(code: MemberExceptionCode, message: String) : super(code.toDetails().copy(message = message))
}
