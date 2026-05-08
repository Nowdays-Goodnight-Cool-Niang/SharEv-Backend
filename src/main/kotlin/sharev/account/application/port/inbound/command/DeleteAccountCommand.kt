package sharev.account.application.port.inbound.command

data class DeleteAccountCommand(
    val accountId: Long,
    val feedback: String
) {
}
