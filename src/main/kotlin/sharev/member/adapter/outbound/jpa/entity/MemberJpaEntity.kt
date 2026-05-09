package sharev.member.adapter.outbound.jpa.entity

import jakarta.persistence.*
import sharev.account.adapter.outbound.jpa.entity.AccountJpaEntity
import sharev.common.adapter.outbound.jpa.entity.BaseTimeEntity
import sharev.member.domain.model.MemberRole
import sharev.member.domain.model.MemberStatus
import sharev.team.adapter.outbound.jpa.entity.TeamJpaEntity

@Entity
@Table(name = "members")
class MemberJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    val team: TeamJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    val account: AccountJpaEntity,

    @Column(columnDefinition = "member_status")
    @Enumerated(EnumType.STRING)
    var status: MemberStatus,

    @Column(columnDefinition = "member_role")
    @Enumerated(EnumType.STRING)
    var role: MemberRole,
) : BaseTimeEntity() {

    fun updateRole(role: MemberRole) {
        this.role = role
    }

    fun activate() {
        this.status = MemberStatus.ACTIVATE
    }
}
