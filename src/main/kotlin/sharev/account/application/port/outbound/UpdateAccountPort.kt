package sharev.account.application.port.outbound

import sharev.account.domain.model.Account

fun interface UpdateAccountPort {
    fun update(accountId: Long, name: String, email: String): Account
}
