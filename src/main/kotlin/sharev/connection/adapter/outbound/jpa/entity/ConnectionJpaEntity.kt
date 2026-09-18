package sharev.connection.adapter.outbound.jpa.entity

import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import sharev.card.adapter.outbound.jpa.entity.CardJpaEntity
import sharev.common.adapter.outbound.jpa.entity.BaseTimeEntity
import sharev.connection.domain.model.ConnectionStatusType

@Entity
@Table(name = "connections")
@EntityListeners(AuditingEntityListener::class)
class ConnectionJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "connection_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "my_card_id")
    val myCard: CardJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "other_card_id")
    val otherCard: CardJpaEntity,

    @Column
    @Enumerated(EnumType.STRING)
    val status: ConnectionStatusType = ConnectionStatusType.REGISTRATION,

    @Column
    val memo: String? = null,
) : BaseTimeEntity() {
    companion object {
        fun connect(
            myCard: CardJpaEntity,
            otherCard: CardJpaEntity
        ): List<ConnectionJpaEntity> {
            return listOf(
                ConnectionJpaEntity(
                    myCard = myCard,
                    otherCard = otherCard
                ),
                ConnectionJpaEntity(
                    myCard = otherCard,
                    otherCard = myCard
                ),
            )
        }
    }
}
