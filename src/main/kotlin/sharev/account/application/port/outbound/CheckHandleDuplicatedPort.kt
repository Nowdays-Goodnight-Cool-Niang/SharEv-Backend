package sharev.account.application.port.outbound

fun interface CheckHandleDuplicatedPort {
    fun isDuplicated(accountId: Long, handle: String): Boolean
}
