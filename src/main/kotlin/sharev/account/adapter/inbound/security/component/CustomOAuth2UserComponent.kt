package sharev.account.adapter.inbound.security.component

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Component
import sharev.account.adapter.inbound.security.mapper.toPrincipal
import sharev.account.application.port.inbound.usecase.OAuthLoginUseCase
import sharev.account.domain.model.OAuthProvider
import sharev.common.domain.exception.BusinessException

@Component
class CustomOAuth2UserComponent(
    private val extractors: List<OAuth2UserInfoExtractor>,
    private val oAuthLoginUseCase: OAuthLoginUseCase,
) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private val defaultOAuth2UserService = DefaultOAuth2UserService()
    private val extractorsByProvider = extractors.associateBy { it.provider }

    init {
        check(extractors.size == extractorsByProvider.size) {
            "중복된 Extractor 구현체가 존재합니다. (Expected: ${extractors.size}, Actual: ${extractorsByProvider.size})"
        }
    }

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = defaultOAuth2UserService.loadUser(userRequest)
        val providerName = userRequest.clientRegistration.registrationId

        val provider = OAuthProvider.fromRegistrationId(providerName)
            ?: throw OAuth2AuthenticationException("지원하지 않는 로그인 제공자입니다: $providerName")

        val extractor = extractorsByProvider[provider]
            ?: throw OAuth2AuthenticationException("지원하지 않는 로그인 제공자입니다: $providerName")

        val command = extractor.extract(oAuth2User)

        return try {
            oAuthLoginUseCase.login(command)
                .toPrincipal(oAuth2User.attributes)
        } catch (exception: BusinessException) {
            throw OAuth2AuthenticationException(
                OAuth2Error("oauth_login_failed"),
                "로그인 도중 문제가 발생하였습니다. 운영진에게 문의해주시기 바랍니다.",
                exception
            )
        }
    }
}
