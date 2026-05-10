package sharev.gathering.adapter.outbound.jpa.entity

import jakarta.persistence.*
import org.hibernate.annotations.SQLRestriction
import org.hibernate.annotations.UuidGenerator
import sharev.common.adapter.outbound.jpa.entity.BaseTimeEntity
import sharev.gathering.domain.model.GatheringVisible
import sharev.team.adapter.outbound.jpa.entity.TeamJpaEntity
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "gatherings")
@SQLRestriction("deleted_at IS NULL")
class GatheringJpaEntity(

    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    @Column(name = "gathering_id")
    val id: UUID? = null,

    @Column(columnDefinition = "visible_type")
    @Enumerated(EnumType.STRING)
    var visible: GatheringVisible,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    val team: TeamJpaEntity,

    @Column
    var title: String,

    @Column
    var content: String,

    @Column
    var startAt: LocalDateTime,

    @Column
    var endAt: LocalDateTime,

    @Column
    var place: String,

    @Column
    var imageUrl: String?,

    @Column
    var gatheringUrl: String?,

    @Column
    var contact: String?,

    @Column
    var deletedAt: LocalDateTime? = null,

    @Column
    var registerStartAt: LocalDateTime,

    @Column
    var registerEndAt: LocalDateTime,
) : BaseTimeEntity() {

    fun update(
        visible: GatheringVisible,
        title: String,
        content: String,
        startAt: LocalDateTime,
        endAt: LocalDateTime,
        place: String,
        imageUrl: String?,
        gatheringUrl: String?,
        contact: String?,
        registerStartAt: LocalDateTime,
        registerEndAt: LocalDateTime,
    ) {
        this.visible = visible
        this.title = title
        this.content = content
        this.startAt = startAt
        this.endAt = endAt
        this.place = place
        this.imageUrl = imageUrl
        this.gatheringUrl = gatheringUrl
        this.contact = contact
        this.registerStartAt = registerStartAt
        this.registerEndAt = registerEndAt
    }

    fun softDelete() {
        deletedAt = LocalDateTime.now()
    }
}
