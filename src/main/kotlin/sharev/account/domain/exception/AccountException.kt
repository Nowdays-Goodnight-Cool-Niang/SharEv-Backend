package sharev.account.domain.exception

import sharev.common.domain.exception.BusinessException

class AccountException : BusinessException {
    constructor(code: AccountExceptionCode) : super(code.toDetails())
    constructor(code: AccountExceptionCode, message: String) : super(code.toDetails().copy(message = message))
}
