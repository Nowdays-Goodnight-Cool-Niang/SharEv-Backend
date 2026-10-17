package sharev.member.application.port.outbound

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sharev.member.domain.model.MemberRole
import sharev.team.domain.model.TeamCertification
import java.time.LocalDateTime

fun interface LoadInvitationPort {
    fun loadAll(accountId: Long, pageable: Pageable): Page<Invitation>
}

data class Invitation(
    val teamId: Long,
    val title: String,
    val content: String?,
    val certification: TeamCertification,
    val role: MemberRole,
    val headcount: Long,
    val createdAt: LocalDateTime,
)
