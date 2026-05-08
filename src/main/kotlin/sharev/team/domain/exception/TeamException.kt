package sharev.team.domain.exception

import sharev.common.domain.exception.BusinessException

class TeamException : BusinessException {
    constructor(code: TeamExceptionCode) : super(code.toDetails())
    constructor(code: TeamExceptionCode, message: String) : super(code.toDetails().copy(message = message))
}
