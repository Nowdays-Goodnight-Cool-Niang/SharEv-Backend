package sharev.team.application.port.outbound.summery

import java.time.LocalDateTime

data class GatheringSummary(
    val title: String,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val place: String?,
)
