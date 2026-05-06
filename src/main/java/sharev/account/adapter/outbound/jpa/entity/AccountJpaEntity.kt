package sharev.account.adapter.outbound.jpa.entity

import jakarta.persistence.*
import sharev.account.domain.model.AccountRole
import sharev.base_entity.BaseTimeEntity

@Entity
@Table(name = "accounts")
class AccountJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    val id: Long? = null,

    @Column
    var name: String,

    @Column
    var email: String,

    @Column
    @Enumerated(EnumType.STRING)
    var role: AccountRole = AccountRole.USER
) : BaseTimeEntity() {
}
