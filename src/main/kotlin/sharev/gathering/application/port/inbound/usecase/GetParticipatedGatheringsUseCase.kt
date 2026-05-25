package sharev.gathering.application.port.inbound.usecase

import sharev.gathering.application.port.inbound.result.GatheringDetailResult

fun interface GetParticipatedGatheringsUseCase {
    fun getParticipatedGatherings(accountId: Long): List<GatheringDetailResult>
}
