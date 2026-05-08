package sharev.team.application.port.outbound

import sharev.team.application.port.outbound.summery.TeamMemberSummary
import sharev.team.application.port.outbound.summery.TeamSummary

interface QueryTeamPort {
    fun findMyTeams(accountId: Long): List<TeamSummary>
    fun findTeamMembers(teamId: Long): List<TeamMemberSummary>
}
