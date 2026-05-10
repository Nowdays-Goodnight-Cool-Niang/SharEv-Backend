package sharev.card.domain.event

import java.util.*

data class ShowCardEvent(
    val eventId: UUID,
    val accountId: Long,
    val targetCardId: Long,
)
