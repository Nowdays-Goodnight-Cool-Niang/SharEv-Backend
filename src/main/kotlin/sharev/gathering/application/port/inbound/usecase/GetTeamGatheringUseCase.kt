package sharev.gathering.application.port.inbound.usecase

import sharev.gathering.application.port.inbound.result.GatheringDetailResult
import java.util.*

interface GetTeamGatheringUseCase {
    fun getTeamGathering(accountId: Long, teamId: Long, gatheringId: UUID): GatheringDetailResult
    fun getTeamGatherings(accountId: Long, teamId: Long): List<GatheringDetailResult>
}
