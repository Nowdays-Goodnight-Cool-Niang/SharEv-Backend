package sharev.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.security.core.authority.FactorGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.security.web.authentication.WebAuthenticationDetails
import sharev.common.adapter.inbound.security.model.AccountPrincipal
import tools.jackson.databind.json.JsonMapper

class RedisConfigTest {
    private val serializer = RedisConfig().springSessionDefaultRedisSerializer()

    @Test
    @DisplayName("카카오 로그인 세션은 Redis에 저장한 뒤 그대로 복원된다")
    fun securityContext_roundTrip() {
        val principal = AccountPrincipal(1L, "USER", "김주호", "test@test.com", "handle", kakaoAttributes())
        val authentication = OAuth2AuthenticationToken(
            principal,
            principal.authorities + FactorGrantedAuthority.fromAuthority(FactorGrantedAuthority.AUTHORIZATION_CODE_AUTHORITY),
            "kakao",
        ).apply { details = WebAuthenticationDetails("127.0.0.1", "session-id") }

        val restored = serializer.deserialize(serializer.serialize(SecurityContextImpl(authentication))) as SecurityContext

        val restoredAuthentication = restored.authentication as OAuth2AuthenticationToken
        assertThat(restoredAuthentication.principal).isEqualTo(principal)
        assertThat(restoredAuthentication.authorizedClientRegistrationId).isEqualTo("kakao")
        assertThat(restoredAuthentication.authorities.map { it.authority })
            .containsExactlyInAnyOrder("ROLE_USER", "ROLE_VERIFIED", FactorGrantedAuthority.AUTHORIZATION_CODE_AUTHORITY)
        assertThat(restoredAuthentication.details).isEqualTo(authentication.details)
    }

    @Test
    @DisplayName("OAuth2 인가 요청은 Redis에 저장한 뒤 그대로 복원된다")
    fun authorizationRequest_roundTrip() {
        val request = OAuth2AuthorizationRequest.authorizationCode()
            .authorizationUri("https://kauth.kakao.com/oauth/authorize")
            .clientId("client-id")
            .redirectUri("http://localhost:8080/login/oauth2/code/kakao")
            .state("state")
            .attributes(mapOf("registration_id" to "kakao"))
            .build()

        val restored = serializer.deserialize(serializer.serialize(request)) as OAuth2AuthorizationRequest

        assertThat(restored.state).isEqualTo("state")
        assertThat(restored.redirectUri).isEqualTo(request.redirectUri)
    }

    // 카카오 사용자 정보 응답을 실제처럼 JSON에서 읽어 LinkedHashMap, Long, Boolean 등이 섞이게 만든다.
    @Suppress("UNCHECKED_CAST")
    private fun kakaoAttributes(): Map<String, Any> = JsonMapper().readValue(
        """
        {
          "id": 3456789012,
          "connected_at": "2025-01-01T00:00:00Z",
          "properties": { "nickname": "김주호" },
          "kakao_account": {
            "profile_nickname_needs_agreement": false,
            "profile": { "nickname": "김주호", "is_default_nickname": false },
            "has_email": true,
            "email": "test@test.com"
          }
        }
        """.trimIndent(),
        Map::class.java,
    ) as Map<String, Any>
}
