package sharev.card.application.port.outbound

import sharev.card.domain.model.Card
import java.util.*

interface LoadCardPort {
    fun loadByGatheringAndAccount(gatheringId: UUID, accountId: Long): Card
    fun loadByGatheringAndPinNumber(gatheringId: UUID, pinNumber: Int): Card
    fun load(cardId: Long): Card
    fun loadUsedPinNumbers(gatheringId: UUID): Set<Int>
    fun existsByGatheringAndAccount(gatheringId: UUID, accountId: Long): Boolean
}
