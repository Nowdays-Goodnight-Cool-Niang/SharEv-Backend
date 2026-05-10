package sharev.team.application.port.inbound.mapper

import sharev.team.application.port.inbound.result.CreateTeamResult
import sharev.team.domain.model.Team

fun Team.toCreateTeamResult() = CreateTeamResult(id)
