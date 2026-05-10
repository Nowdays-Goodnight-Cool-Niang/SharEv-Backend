package sharev.gathering.application.port.inbound.usecase

import sharev.gathering.application.port.inbound.result.GatheringDetailResult
import java.util.*

interface GetGatheringUseCase {
    fun getGathering(accountId: Long, teamId: Long, gatheringId: UUID): GatheringDetailResult
    fun getGatherings(accountId: Long, teamId: Long): List<GatheringDetailResult>
}
