package sharev.account.adapter.outbound.jpa.entity

import jakarta.persistence.*
import sharev.account.domain.model.OAuthProvider
import sharev.base_entity.BaseTimeEntity

@Entity
@Table(name = "oauth_accounts")
@IdClass(OAuthAccountJpaEntityId::class)
class OAuthAccountJpaEntity(

    @Id
    @Enumerated(EnumType.STRING)
    val provider: OAuthProvider,

    @Id
    @Column(name = "subject_identifier")
    val subjectIdentifier: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    val account: AccountJpaEntity

) : BaseTimeEntity() {
}
