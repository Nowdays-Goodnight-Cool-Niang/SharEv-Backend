package sharev.account.application.port.inbound.command

data class UpdateAccountHandleCommand(
    val accountId: Long,
    val handle: String,
) {
}
