package sharev.gathering.application.port.inbound.usecase

import sharev.gathering.application.port.inbound.result.ParticipantResult
import java.util.*

fun interface CheckGatheringParticipantUseCase {
    fun isParticipant(accountId: Long, gatheringId: UUID): ParticipantResult
}
