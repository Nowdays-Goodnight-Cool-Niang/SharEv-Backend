package sharev

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.test.context.support.WithSecurityContextFactory
import sharev.common.adapter.inbound.security.model.AccountPrincipal

class WithCustomMockUserSecurityContextFactory : WithSecurityContextFactory<WithCustomMockUser> {
    override fun createSecurityContext(annotation: WithCustomMockUser): SecurityContext {
        val account = AccountPrincipal(1L, "USER", "test", "test@test.com", emptyMap())
        val token = OAuth2AuthenticationToken(
            account,
            listOf(SimpleGrantedAuthority(annotation.role)),
            "kakao",
        )
        return SecurityContextHolder.createEmptyContext().apply {
            authentication = token
        }
    }
}
