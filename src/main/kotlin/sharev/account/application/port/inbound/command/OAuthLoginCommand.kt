package sharev.account.application.port.inbound.command

import sharev.account.domain.model.OAuthProvider

data class OAuthLoginCommand(
    val provider: OAuthProvider,
    val subjectIdentifier: String,
    val name: String,
    val email: String,
) {
}
