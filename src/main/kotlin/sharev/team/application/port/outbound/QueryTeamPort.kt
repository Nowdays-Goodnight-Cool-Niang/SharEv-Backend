package sharev.team.application.port.outbound

import sharev.team.application.port.outbound.summary.MyTeamSummary
import sharev.team.application.port.outbound.summary.TeamMemberSummary

interface QueryTeamPort {
    fun findMyTeams(accountId: Long): List<MyTeamSummary>
    fun findTeamMembers(teamId: Long): List<TeamMemberSummary>
}
