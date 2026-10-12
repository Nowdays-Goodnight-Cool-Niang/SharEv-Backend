package sharev.team.application.port.outbound

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface LoadJoinedTeamPort {
    fun load(accountId: Long, pageable: Pageable): Page<JoinedTeam>
    fun headcount(teamIds: Collection<Long>): Map<Long, Long>
}

data class JoinedTeam(
    val teamId: Long,
    val memberRole: String,
) {
}
