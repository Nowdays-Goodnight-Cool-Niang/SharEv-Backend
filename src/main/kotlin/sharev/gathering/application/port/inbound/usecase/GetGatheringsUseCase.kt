package sharev.gathering.application.port.inbound.usecase

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sharev.gathering.application.port.inbound.command.GetGatheringCommand
import sharev.gathering.application.port.inbound.result.GatheringResult

fun interface GetGatheringsUseCase {
    fun getGatherings(command: GetGatheringCommand, pageable: Pageable): Page<GatheringResult>
}
