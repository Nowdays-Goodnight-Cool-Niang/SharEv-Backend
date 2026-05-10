package sharev.account.application.port.inbound.mapper

import sharev.account.application.port.inbound.result.UpdateAccountInfoResult
import sharev.account.domain.model.Account

fun Account.toUpdateAccountInfoResult() = UpdateAccountInfoResult(id, name, email)
