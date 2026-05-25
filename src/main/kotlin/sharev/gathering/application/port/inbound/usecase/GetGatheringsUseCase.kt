package sharev.gathering.application.port.inbound.usecase

import sharev.gathering.application.port.inbound.result.GatheringDetailResult

fun interface GetGatheringsUseCase {
    fun getGatherings(): List<GatheringDetailResult>
}
