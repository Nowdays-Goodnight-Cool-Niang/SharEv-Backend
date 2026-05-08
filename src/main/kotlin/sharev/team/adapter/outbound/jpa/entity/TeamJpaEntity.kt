package sharev.team.adapter.outbound.jpa.entity

import jakarta.persistence.*
import sharev.common.adapter.outbound.jpa.entity.BaseTimeEntity
import sharev.team.domain.model.TeamCertification

@Entity
@Table(name = "teams")
class TeamJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    val id: Long? = null,

    @Column(columnDefinition = "team_certification")
    @Enumerated(EnumType.STRING)
    var teamCertification: TeamCertification = TeamCertification.NONE,

    @Column(unique = true)
    var title: String,

    @Column
    var content: String? = null,

    @Column
    var activateFlag: Boolean? = null,
) : BaseTimeEntity() {

    fun updateTitle(title: String) {
        this.title = title
    }
}
