package sharev.team.adapter.outbound.jpa.entity

import jakarta.persistence.*
import org.hibernate.annotations.SoftDelete
import org.hibernate.annotations.SoftDeleteType
import sharev.common.adapter.outbound.jpa.entity.BaseTimeEntity
import sharev.team.domain.model.TeamCertification

@Entity
@Table(name = "teams")
@SoftDelete(columnName = "deleted_at", strategy = SoftDeleteType.TIMESTAMP)
class TeamJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    val id: Long? = null,

    @Column
    var title: String,

    @Column
    var content: String?,

    @Column
    @Enumerated(EnumType.STRING)
    var certification: TeamCertification = TeamCertification.NONE,
) : BaseTimeEntity() {

    fun update(title: String, content: String?) {
        this.title = title
        this.content = content
    }
}
