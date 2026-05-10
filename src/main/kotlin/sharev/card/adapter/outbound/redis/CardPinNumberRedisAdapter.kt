package sharev.card.adapter.outbound.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import sharev.card.application.port.outbound.CardPinNumberPoolPort
import sharev.card.domain.exception.CardException
import sharev.common.adapter.outbound.redisson.LockProcessor
import java.time.Duration
import java.util.*
import java.util.stream.IntStream
import sharev.card.domain.exception.CardExceptionCode as CardCode

@Component
class CardPinNumberRedisAdapter(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val lockProcessor: LockProcessor,
) : CardPinNumberPoolPort {

    override fun popAvailablePinNumber(
        gatheringId: UUID,
        usedPinNumbersProvider: () -> Collection<Int>,
    ): Int? {
        val eventPinKey = calculateEventPinKey(gatheringId)
        val pinNumber = redisTemplate.opsForSet().pop(eventPinKey) as Int?

        if (pinNumber != null) {
            return pinNumber
        }

        lockProcessor.lock(eventPinKey) { key, isLockHeld ->
            initializePinNumbers(
                key,
                usedPinNumbersProvider,
                isLockHeld
            )
        }

        return redisTemplate.opsForSet().pop(eventPinKey) as Int?
    }

    override fun restorePinNumber(gatheringId: UUID, pinNumber: Int) {
        redisTemplate.opsForSet().add(calculateEventPinKey(gatheringId), pinNumber)
    }

    private fun calculateEventPinKey(gatheringId: UUID): String = "gathering:$gatheringId:pin-numbers"

    private fun initializePinNumbers(
        eventPinKey: String,
        usedPinNumbersProvider: () -> Collection<Int>,
        isLockHeld: () -> Boolean,
    ) {
        val keyExistFlag = redisTemplate.hasKey(eventPinKey)
            ?: throw CardException(CardCode.KEY_ERROR)

        if (keyExistFlag) {
            return
        }

        val usedPinNumbers = usedPinNumbersProvider()

        if (!isLockHeld()) {
            return
        }

        val availablePinNumbers = IntStream.rangeClosed(
            START_PIN_RANGE,
            END_PIN_RANGE
        )
            .filter { !usedPinNumbers.contains(it) }
            .boxed()
            .toArray { size -> arrayOfNulls<Int>(size) }

        redisTemplate.opsForSet().add(eventPinKey, *availablePinNumbers)
        redisTemplate.expire(
            eventPinKey,
            Duration.ofDays(EXPIRE_PIN_NUMBER_DAY)
        )
    }

    private companion object {
        const val START_PIN_RANGE = 1000
        const val END_PIN_RANGE = 9999
        const val EXPIRE_PIN_NUMBER_DAY = 3L
    }
}
