package sharev.gathering.application.port.inbound.usecase

import sharev.gathering.application.port.inbound.result.DeleteGatheringResult
import java.util.*

fun interface DeleteGatheringUseCase {
    fun delete(accountId: Long, teamId: Long, gatheringId: UUID): DeleteGatheringResult
}
