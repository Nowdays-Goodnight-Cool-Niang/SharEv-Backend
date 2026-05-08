package sharev.account.application.port.outbound

fun interface DeleteAccountPort {
    fun delete(accountId: Long)
}
