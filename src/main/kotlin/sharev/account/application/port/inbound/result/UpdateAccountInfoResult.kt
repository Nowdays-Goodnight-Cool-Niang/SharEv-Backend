package sharev.account.application.port.inbound.result

data class UpdateAccountInfoResult(
    val id: Long,
    val name: String,
    val email: String,
) {
}
