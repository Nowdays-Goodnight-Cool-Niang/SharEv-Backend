package sharev.card.application.port.outbound

import java.util.*

interface CardPinNumberPoolPort {
    fun popAvailablePinNumber(
        gatheringId: UUID,
        usedPinNumbersProvider: () -> Collection<Int>,
    ): Int?

    fun restorePinNumber(gatheringId: UUID, pinNumber: Int)
}
