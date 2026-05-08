package sharev.link.domain.exception

import sharev.common.domain.exception.BusinessException

class LinkException : BusinessException {
    constructor(code: LinkExceptionCode) : super(code.toDetails())
    constructor(code: LinkExceptionCode, message: String) : super(code.toDetails().copy(message = message))
}
