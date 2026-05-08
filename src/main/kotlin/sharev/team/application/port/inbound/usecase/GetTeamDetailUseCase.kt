package sharev.team.application.port.inbound.usecase

import sharev.team.application.port.inbound.result.TeamDetailResult

fun interface GetTeamDetailUseCase {
    fun getTeamDetail(accountId: Long, teamId: Long): TeamDetailResult
}
