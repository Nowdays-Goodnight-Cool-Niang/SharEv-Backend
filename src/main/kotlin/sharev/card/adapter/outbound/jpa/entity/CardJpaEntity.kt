package sharev.card.adapter.outbound.jpa.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import sharev.account.adapter.outbound.jpa.entity.AccountJpaEntity
import sharev.gathering.adapter.outbound.jpa.entity.GatheringJpaEntity

@Entity
@Table(name = "cards")
class CardJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id")
    val gathering: GatheringJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    val account: AccountJpaEntity,

    @Column
    var pinNumber: Int?,

    @Column
    var templateVersion: Int? = null,

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    var introductionText: Map<String, String>? = null,
) : sharev.common.adapter.outbound.jpa.entity.BaseTimeEntity() {

    fun updateIntroductionText(templateVersion: Int, introductionText: Map<String, String>) {
        this.templateVersion = templateVersion
        this.introductionText = introductionText
    }
}
