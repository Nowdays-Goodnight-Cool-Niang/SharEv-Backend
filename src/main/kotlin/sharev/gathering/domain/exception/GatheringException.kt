package sharev.gathering.domain.exception

import sharev.common.domain.exception.BusinessException

class GatheringException : BusinessException {
    constructor(code: sharev.gathering.domain.exception.GatheringExceptionCode) : super(code.toDetails())
    constructor(
        code: sharev.gathering.domain.exception.GatheringExceptionCode,
        message: String
    ) : super(code.toDetails().copy(message = message))
}
