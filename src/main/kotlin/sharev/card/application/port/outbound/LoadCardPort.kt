package sharev.card.application.port.outbound

import sharev.card.domain.model.Card
import java.util.*

interface LoadCardPort {
    fun loadByGatheringAndAccount(gatheringId: UUID, accountId: Long): Card
    fun loadByGatheringAndJoinOrder(gatheringId: UUID, joinOrder: Int): Card
    fun load(cardId: Long): Card
    fun existsByGatheringAndAccount(gatheringId: UUID, accountId: Long): Boolean
}
