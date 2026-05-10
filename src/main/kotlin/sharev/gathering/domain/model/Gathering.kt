package sharev.gathering.domain.model

import java.time.LocalDateTime
import java.util.*

data class Gathering(
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
) {
    companion object {
        val NEW_ID: UUID = UUID(0L, 0L)
    }
}
