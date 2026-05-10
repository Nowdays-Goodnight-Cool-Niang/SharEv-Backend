package sharev.gathering.adapter.inbound.web.dto.response

import java.time.LocalDateTime
import java.util.*

data class CreateGatheringResponse(
    val id: UUID,
    val teamId: Long,
    val visible: String,
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
