package sharev.account.application.port.outbound

import sharev.account.domain.model.Account

fun interface LoadAccountPort {
    fun load(accountId: Long): Account
}
