package sharev.account.application.port.inbound.result

import sharev.account.domain.model.AccountRole

data class OAuthLoginResult(
    val id: Long,
    val role: AccountRole,
    val name: String,
    val email: String,
    val handle: String?,
) {
}
