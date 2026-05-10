package sharev.gathering.domain.exception

import sharev.common.domain.exception.BusinessException

class GatheringException : BusinessException {
    constructor(code: GatheringExceptionCode) : super(code.toDetails())
    constructor(
        code: GatheringExceptionCode,
        message: String
    ) : super(code.toDetails().copy(message = message))
}
