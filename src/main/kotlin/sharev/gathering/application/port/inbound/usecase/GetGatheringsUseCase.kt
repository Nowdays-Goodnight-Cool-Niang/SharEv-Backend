package sharev.gathering.application.port.inbound.usecase

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sharev.gathering.application.port.inbound.command.GetGatheringCommand
import sharev.gathering.application.port.inbound.result.GatheringDetailResult

fun interface GetGatheringsUseCase {
    fun getGatherings(getGatheringCommand: GetGatheringCommand, pageable: Pageable): Page<GatheringDetailResult>
}
