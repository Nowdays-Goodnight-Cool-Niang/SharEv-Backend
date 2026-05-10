package sharev.common.domain.exception

class LockException : BusinessException {
    constructor(code: LockExceptionCode) : super(code.toDetails())
    constructor(code: LockExceptionCode, message: String) : super(code.toDetails().copy(message = message))
}
