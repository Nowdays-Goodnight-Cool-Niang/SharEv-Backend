package sharev.card.application.port.inbound.usecase

import sharev.card.application.port.inbound.command.IsJoinedCommand
import sharev.card.application.port.inbound.result.ParticipantFlagResult

fun interface IsJoinedUseCase {
    fun isJoined(command: IsJoinedCommand): ParticipantFlagResult
}
