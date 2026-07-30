package sharev.account.adapter.inbound.web.dto.response

data class UpdateAccountInfoResponse(
    val id: Long,
    val name: String,
    val email: String,
) {
}
