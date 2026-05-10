package sharev.gathering.application.port.inbound.result

import sharev.gathering.domain.model.GatheringVisible
import java.time.LocalDateTime
import java.util.*

data class CreateGatheringResult(
    val id: UUID,
    val teamId: Long,
    val visible: GatheringVisible,
    val title: String,
    val content: String,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val place: String,
    val imageUrl: String?,
    val gatheringUrl: String?,
    val contact: String?,
    val registerStartAt: LocalDateTime,
    val registerEndAt: LocalDateTime,
)
