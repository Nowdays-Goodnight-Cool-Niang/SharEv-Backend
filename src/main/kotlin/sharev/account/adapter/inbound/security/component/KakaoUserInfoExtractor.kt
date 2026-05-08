package sharev.account.adapter.inbound.security.component

import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Component
import sharev.account.application.port.inbound.command.OAuthLoginCommand
import sharev.account.domain.model.OAuthProvider

@Component
class KakaoUserInfoExtractor : OAuth2UserInfoExtractor {
    override val provider: OAuthProvider = OAuthProvider.KAKAO

    override fun extract(oAuth2User: OAuth2User): OAuthLoginCommand {
        val subjectIdentifier = calculateSubjectIdentifier(oAuth2User)

        val kakaoUserInfo = oAuth2User.attributes["kakao_account"] as? Map<*, *>
            ?: throw OAuth2AuthenticationException("카카오 로그인 도중 문제가 발생했습니다. 운영진에게 문의해주시기 바랍니다.");

        val name = calculateName(kakaoUserInfo)
        val email = calculateEmail(kakaoUserInfo)

        return OAuthLoginCommand(provider, subjectIdentifier, name, email)
    }

    override fun calculateSubjectIdentifier(oAuth2User: OAuth2User): String {
        return oAuth2User.attributes["id"]
            ?.toString()
            ?.takeIf { it.isNotBlank() }
            ?: throw OAuth2AuthenticationException("카카오 로그인 도중 문제가 발생했습니다. 운영진에게 문의해주시기 바랍니다.")
    }

    override fun calculateName(attributes: Map<*, *>): String {
        val profile = attributes["profile"] as? Map<*, *>

        return (profile?.get("nickname") as? String)
            ?.takeIf { it.isNotBlank() }
            ?: ""
    }

    override fun calculateEmail(attributes: Map<*, *>): String {
        return (attributes["email"] as? String)
            ?.takeIf { it.isNotBlank() }
            ?: ""
    }
}
