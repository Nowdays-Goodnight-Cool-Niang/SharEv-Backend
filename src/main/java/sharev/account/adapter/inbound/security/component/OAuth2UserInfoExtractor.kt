package sharev.account.adapter.inbound.security.component

import org.springframework.security.oauth2.core.user.OAuth2User
import sharev.account.application.port.inbound.command.OAuthLoginCommand
import sharev.account.domain.model.OAuthProvider

interface OAuth2UserInfoExtractor {
    val provider: OAuthProvider

    fun calculateSubjectIdentifier(oAuth2User: OAuth2User): String
    fun calculateName(attributes: Map<*, *>): String
    fun calculateEmail(attributes: Map<*, *>): String
    fun extract(oAuth2User: OAuth2User): OAuthLoginCommand
}
