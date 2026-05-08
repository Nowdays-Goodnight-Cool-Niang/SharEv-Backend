package sharev.team.adapter.outbound.jpa.repository

import sharev.team.application.port.outbound.summery.TeamMemberSummary
import sharev.team.application.port.outbound.summery.TeamSummary

interface TeamRepositoryCustom {
    fun findMyTeams(accountId: Long): List<TeamSummary>

    fun findMyTeamMembers(teamId: Long): List<TeamMemberSummary>
}
