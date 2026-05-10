package sharev.card.application.port.outbound

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import sharev.card.application.port.outbound.result.TempCard
import java.time.LocalDateTime
import java.util.*

fun interface QueryCardPort {
    fun searchTempCards(
        gatheringId: UUID,
        myCardId: Long,
        snapshotTime: LocalDateTime,
        pageable: Pageable,
    ): Page<TempCard>
}
