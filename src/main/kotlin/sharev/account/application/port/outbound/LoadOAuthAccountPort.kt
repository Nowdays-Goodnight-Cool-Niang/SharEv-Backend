package sharev.account.application.port.outbound

import sharev.account.domain.model.OAuthAccount
import sharev.account.domain.model.OAuthProvider

fun interface LoadOAuthAccountPort {
    fun load(provider: OAuthProvider, subjectIdentifier: String): OAuthAccount?
}
