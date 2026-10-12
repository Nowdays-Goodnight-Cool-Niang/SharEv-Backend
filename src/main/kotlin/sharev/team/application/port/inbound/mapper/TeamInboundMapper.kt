package sharev.team.application.port.inbound.mapper

import sharev.team.application.port.inbound.result.CreateTeamResult
import sharev.team.application.port.inbound.result.JoinedTeamResult
import sharev.team.application.port.outbound.JoinedTeam
import sharev.team.domain.model.Team

fun Team.toCreateTeamResult() = CreateTeamResult(id)

fun JoinedTeam.toResult(team: Team, headcount: Long) = JoinedTeamResult(
    teamId,
    team.title,
    team.content,
    team.createdAt,
    memberRole,
    headcount
)
