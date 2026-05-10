package sharev.gathering.application.port.outbound

import java.util.*

fun interface CheckGatheringParticipantPort {
    fun isParticipant(gatheringId: UUID, accountId: Long): Boolean
}
