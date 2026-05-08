package sharev.account.application.port.outbound

import sharev.account.domain.model.Account

fun interface SaveAccountPort {
    fun save(account: Account): Account
}
