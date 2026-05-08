package sharev.link.adapter.outbound.jpa.entity

import jakarta.persistence.*
import sharev.account.adapter.outbound.jpa.entity.AccountJpaEntity
import sharev.common.adapter.outbound.jpa.entity.BaseTimeEntity

@Entity
@Table(name = "links")
class LinkJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "link_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    val account: AccountJpaEntity,

    @Column
    val linkUrl: String,
) : BaseTimeEntity() {
}
