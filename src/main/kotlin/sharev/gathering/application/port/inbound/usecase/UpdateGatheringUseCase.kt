package sharev.gathering.application.port.inbound.usecase

import sharev.gathering.application.port.inbound.command.UpdateGatheringCommand
import sharev.gathering.application.port.inbound.result.GatheringDetailResult

fun interface UpdateGatheringUseCase {
    fun update(command: UpdateGatheringCommand): GatheringDetailResult
}
