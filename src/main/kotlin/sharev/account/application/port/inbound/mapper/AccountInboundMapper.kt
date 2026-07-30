package sharev.account.application.port.inbound.mapper

import sharev.account.application.port.inbound.result.UpdateAccountHandleResult
import sharev.account.application.port.inbound.result.UpdateAccountInfoResult
import sharev.account.domain.model.Account

fun Account.toUpdateAccountInfoResult() = UpdateAccountInfoResult(id, name, email)

fun Account.toUpdateAccountHandleResult() = UpdateAccountHandleResult(
    checkNotNull(handle) { "handle 업데이트 직후에는 null이어선 안 됩니다." }
)
