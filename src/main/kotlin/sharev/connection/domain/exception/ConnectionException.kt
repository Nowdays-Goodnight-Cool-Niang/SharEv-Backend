package sharev.connection.domain.exception

import sharev.common.domain.exception.BusinessException

class ConnectionException : BusinessException {
    constructor(code: ConnectionExceptionCode) : super(code.toDetails())
    constructor(
        code: ConnectionExceptionCode,
        message: String
    ) : super(code.toDetails().copy(message = message))
}
