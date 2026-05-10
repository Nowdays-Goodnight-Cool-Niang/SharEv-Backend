package sharev.team.application.port.outbound

import sharev.team.application.port.outbound.summary.TeamMemberSummary
import sharev.team.application.port.outbound.summary.TeamSummary

interface QueryTeamPort {
    fun findMyTeams(accountId: Long): List<TeamSummary>
    fun findTeamMembers(teamId: Long): List<TeamMemberSummary>
}
