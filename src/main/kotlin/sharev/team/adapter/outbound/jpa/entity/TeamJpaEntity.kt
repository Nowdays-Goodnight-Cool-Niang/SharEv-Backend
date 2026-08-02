package sharev.team.adapter.outbound.jpa.entity

import jakarta.persistence.*
import org.hibernate.annotations.SoftDelete
import org.hibernate.annotations.SoftDeleteType
import sharev.common.adapter.outbound.jpa.entity.BaseTimeEntity
import sharev.team.domain.model.TeamCertification
import sharev.team.domain.model.TeamType

@Entity
@Table(name = "teams")
@SoftDelete(columnName = "deleted_at", strategy = SoftDeleteType.TIMESTAMP)
class TeamJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    val id: Long? = null,

    @Column
    @Enumerated(EnumType.STRING)
    var certification: TeamCertification = TeamCertification.NONE,

    @Column
    @Enumerated(EnumType.STRING)
    val type: TeamType,

    @Column(unique = true)
    var title: String?,

    @Column
    var content: String,
) : BaseTimeEntity() {

    fun update(title: String, content: String) {
        this.title = title
        this.content = content
    }
}
