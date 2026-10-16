package sharev.gathering.application.port.inbound.usecase

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sharev.gathering.application.port.inbound.result.ManagedGatheringDetailResult

fun interface GetManagedGatheringsUseCase {
    fun getManagedGatherings(accountId: Long, pageable: Pageable): Page<ManagedGatheringDetailResult>
}
