package sharev.card.domain.model

import sharev.card.domain.exception.CardException
import sharev.card.domain.exception.CardExceptionCode
import java.nio.ByteBuffer
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class ShareCodeConverter(secret: ByteArray) {
    private companion object {
        const val ALGORITHM = "HmacSHA256"
        const val ROUNDS = 8
        const val HIGH_MODULUS = 90
        const val LOW_MODULUS = 100
        const val CAPACITY = HIGH_MODULUS * LOW_MODULUS
        const val SHARE_CODE_OFFSET = 1_000
    }

    private val key = SecretKeySpec(secret, ALGORITHM)

    fun toShareCode(gatheringId: UUID, joinOrder: Int): Int {
        if (joinOrder !in 0 until CAPACITY) {
            throw CardException(CardExceptionCode.JOIN_CAPACITY_EXCEEDED)
        }

        val permuted = (0 until ROUNDS).fold(joinOrder) { number, round ->
            feistelRound(gatheringId, number, round, Int::plus)
        }

        return permuted + SHARE_CODE_OFFSET
    }

    fun toJoinOrder(gatheringId: UUID, shareCode: Int): Int {
        val permuted = shareCode - SHARE_CODE_OFFSET

        if (permuted !in 0 until CAPACITY) {
            throw CardException(CardExceptionCode.CARD_NOT_FOUND)
        }

        return (ROUNDS - 1 downTo 0).fold(permuted) { number, round ->
            feistelRound(gatheringId, number, round, Int::minus)
        }
    }

    private fun feistelRound(gatheringId: UUID, number: Int, round: Int, operator: (Int, Int) -> Int): Int {
        val high = number / LOW_MODULUS
        val low = number % LOW_MODULUS

        if (round % 2 == 0) {
            val shifted = operator(low, roundFunction(gatheringId, round, high, LOW_MODULUS))
            val newLow = Math.floorMod(shifted, LOW_MODULUS)
            return high * LOW_MODULUS + newLow
        }

        val shifted = operator(high, roundFunction(gatheringId, round, low, HIGH_MODULUS))
        val newHigh = Math.floorMod(shifted, HIGH_MODULUS)
        return newHigh * LOW_MODULUS + low
    }

    private fun roundFunction(gatheringId: UUID, round: Int, half: Int, modulus: Int): Int {
        val message = "$gatheringId:$round:$half".toByteArray()
        val hash = calculateKeyedHash(message)
        val number = ByteBuffer.wrap(hash).getInt()
        return Math.floorMod(number, modulus)
    }

    private fun calculateKeyedHash(message: ByteArray): ByteArray =
        Mac.getInstance(ALGORITHM)
            .apply { init(key) }
            .doFinal(message)
}
