package sharev.gathering.application.port.inbound.usecase

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sharev.gathering.application.port.inbound.command.GetGatheringCommand
import sharev.gathering.application.port.inbound.result.GatheringDetailResult
import sharev.gathering.application.port.inbound.result.GatheringResult

interface GetGatheringsUseCase {
    fun getGatherings(command: GetGatheringCommand, pageable: Pageable): Page<GatheringResult>
    fun getManagedGatherings(accountId: Long, pageable: Pageable): Page<GatheringDetailResult>
}
