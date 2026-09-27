package sharev.gathering.application.port.inbound.usecase

import sharev.gathering.application.port.inbound.result.GatheringDetailResult
import java.util.*

fun interface GetGatheringUseCase {
    fun getGathering(accountId: Long?, gatheringId: UUID): GatheringDetailResult
}
