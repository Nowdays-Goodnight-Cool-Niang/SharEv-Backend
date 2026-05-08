package sharev.common.adapter.inbound.security.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User

data class AccountPrincipal(
    val id: Long,
    val role: String,
    val accountName: String,
    val email: String,
    private val attributes: Map<String, Any>,
) : OAuth2User {

    private val authorities = listOf(SimpleGrantedAuthority("ROLE_$role"))

    override fun getAttributes(): Map<String, Any> = attributes

    override fun getAuthorities(): Collection<GrantedAuthority> = authorities

    override fun getName(): String = id.toString()
}

