package sharev.team.adapter.outbound.jpa.repository

import sharev.team.application.port.outbound.summary.MyTeamSummary
import sharev.team.application.port.outbound.summary.TeamMemberSummary

interface TeamRepositoryCustom {
    fun findMyTeams(accountId: Long): List<MyTeamSummary>

    fun findMyTeamMembers(teamId: Long): List<TeamMemberSummary>
}
