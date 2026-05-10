package sharev.team.application.port.inbound.usecase

import sharev.team.application.port.inbound.command.GetMyTeamsCommand
import sharev.team.application.port.inbound.result.TeamInfoResult

fun interface GetMyTeamsUseCase {
    fun getMyTeams(command: GetMyTeamsCommand): List<TeamInfoResult>
}
