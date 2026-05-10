package sharev.gathering.application.port.inbound.usecase

import sharev.gathering.application.port.inbound.command.CreateGatheringCommand
import sharev.gathering.application.port.inbound.result.CreateGatheringResult

fun interface CreateGatheringUseCase {
    fun create(command: CreateGatheringCommand): CreateGatheringResult
}
