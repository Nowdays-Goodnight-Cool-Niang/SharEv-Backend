package sharev.account.application.port.outbound

import sharev.account.domain.model.Account

fun interface UpdateAccountHandlePort {
    fun update(accountId: Long, handle: String): Account
}
