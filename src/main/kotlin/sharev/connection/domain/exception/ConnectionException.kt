package sharev.connection.domain.exception

import sharev.common.domain.exception.BusinessException

class ConnectionException : BusinessException {
    constructor(code: sharev.connection.domain.exception.ConnectionExceptionCode) : super(code.toDetails())
    constructor(
        code: sharev.connection.domain.exception.ConnectionExceptionCode,
        message: String
    ) : super(code.toDetails().copy(message = message))
}
