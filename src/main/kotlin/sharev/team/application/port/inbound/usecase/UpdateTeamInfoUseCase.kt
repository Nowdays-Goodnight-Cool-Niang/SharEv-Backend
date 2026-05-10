package sharev.team.application.port.inbound.usecase

import sharev.team.application.port.inbound.command.UpdateTeamInfoCommand
import sharev.team.application.port.inbound.result.TeamUpdateInfoResult

fun interface UpdateTeamInfoUseCase {
    fun updateTeamInfo(command: UpdateTeamInfoCommand): TeamUpdateInfoResult
}
