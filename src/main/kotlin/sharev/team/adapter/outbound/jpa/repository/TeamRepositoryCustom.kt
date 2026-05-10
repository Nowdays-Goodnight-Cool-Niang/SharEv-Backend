package sharev.team.adapter.outbound.jpa.repository

import sharev.team.application.port.outbound.summary.TeamMemberSummary
import sharev.team.application.port.outbound.summary.TeamSummary

interface TeamRepositoryCustom {
    fun findMyTeams(accountId: Long): List<TeamSummary>

    fun findMyTeamMembers(teamId: Long): List<TeamMemberSummary>
}
