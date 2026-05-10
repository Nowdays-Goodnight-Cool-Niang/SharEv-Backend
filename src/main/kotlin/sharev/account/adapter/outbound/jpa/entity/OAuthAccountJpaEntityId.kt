package sharev.account.adapter.outbound.jpa.entity

import sharev.account.domain.model.OAuthProvider
import java.io.Serializable

data class OAuthAccountJpaEntityId(
    val provider: OAuthProvider? = null,
    val subjectIdentifier: String? = null
) : Serializable
