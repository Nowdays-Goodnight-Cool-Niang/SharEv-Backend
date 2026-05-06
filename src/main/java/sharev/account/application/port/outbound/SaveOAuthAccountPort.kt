package sharev.account.application.port.outbound

import sharev.account.domain.model.OAuthAccount

fun interface SaveOAuthAccountPort {
    fun save(oAuthAccount: OAuthAccount): OAuthAccount
}
