package sharev.card.application.port.inbound.command

import org.springframework.data.domain.Pageable
import java.time.LocalDateTime
import java.util.*

data class GetAllCardsCommand(
    val gatheringId: UUID,
    val accountId: Long,
    val snapshotTime: LocalDateTime,
    val pageable: Pageable,
)
