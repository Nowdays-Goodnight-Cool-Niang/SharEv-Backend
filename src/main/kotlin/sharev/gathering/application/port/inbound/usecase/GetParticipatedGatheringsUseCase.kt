package sharev.gathering.application.port.inbound.usecase

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sharev.gathering.application.port.inbound.result.GatheringDetailResult

fun interface GetParticipatedGatheringsUseCase {
    fun getParticipatedGatherings(accountId: Long, pageable: Pageable): Page<GatheringDetailResult>
}
