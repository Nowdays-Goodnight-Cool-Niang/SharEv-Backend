package sharev.account.application.port.inbound.command

data class UpdateAccountInfoCommand(
    val accountId: Long,
    val name: String,
    val email: String,
) {
}
