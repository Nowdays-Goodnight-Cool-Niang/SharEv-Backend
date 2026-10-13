package sharev.card.domain.model

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import sharev.card.domain.exception.CardException
import sharev.card.domain.exception.CardExceptionCode
import java.util.*

class ShareCodeConverterTest {
    private val secret = ByteArray(32) { it.toByte() }
    private val converter = ShareCodeConverter(secret)
    private val gatheringId = UUID.randomUUID()

    @Test
    fun `0~8999까지 joinOrder를 건넨 경우 모두 다른 1000~9999의 shareCode가 나와야 한다`() {
        val shareCodes = (0..8999).map { joinOrder -> converter.toShareCode(gatheringId, joinOrder) }
            .toSet()

        Assertions.assertThat(shareCodes).isEqualTo((1000..9999).toSet())
    }

    @Test
    fun `1000~9999까지 shareCode를 건넨 경우 모두 다른 0~8999의 joinOrder가 나와야 한다`() {
        val joinOrders = (1000..9999).map { shareCode -> converter.toJoinOrder(gatheringId, shareCode) }
            .toSet()

        Assertions.assertThat(joinOrders).isEqualTo((0..8999).toSet())
    }

    @Test
    fun `변환을 거쳐도 joinOrder는 달라져선 안된다`() {
        (0..8999).forEach { joinOrder ->
            val shareCode = converter.toShareCode(gatheringId, joinOrder)
            val afterJoinOrder = converter.toJoinOrder(gatheringId, shareCode)

            Assertions.assertThat(afterJoinOrder).isEqualTo(joinOrder)
        }
    }

    @Test
    fun `변환을 거쳐도 shareCode는 달라져선 안된다`() {
        (1000..9999).forEach { shareCode ->
            val joinOrder = converter.toJoinOrder(gatheringId, shareCode)
            val afterShareCode = converter.toShareCode(gatheringId, joinOrder)

            Assertions.assertThat(afterShareCode).isEqualTo(shareCode)
        }
    }

    @Test
    fun `행사가 같다면 shareCode는 같게 나와야 한다`() {
        val joinOrders = (0..8999).toList()

        val shareCodes = joinOrders.map { converter.toShareCode(gatheringId, it) }
        val otherShareCodes = joinOrders.map { converter.toShareCode(gatheringId, it) }

        Assertions.assertThat(otherShareCodes).isEqualTo(shareCodes)
    }

    @Test
    fun `행사가 다르다면 shareCode도 다르게 나와야 한다`() {
        val joinOrders = (0..8999).toList()

        val shareCodes = joinOrders.map { converter.toShareCode(gatheringId, it) }
        val otherShareCodes = joinOrders.map { converter.toShareCode(UUID.randomUUID(), it) }

        Assertions.assertThat(otherShareCodes).isNotEqualTo(shareCodes)
    }

    @Test
    fun `키가 같다면 shareCode는 같게 나와야 한다`() {
        val otherConverter = ShareCodeConverter(secret)
        val joinOrders = (0..8999).toList()

        val shareCodes = joinOrders.map { converter.toShareCode(gatheringId, it) }
        val otherShareCodes = joinOrders.map { otherConverter.toShareCode(gatheringId, it) }

        Assertions.assertThat(otherShareCodes).isEqualTo(shareCodes)
    }

    @Test
    fun `키가 다르다면 shareCode도 다르게 나와야 한다`() {
        val otherConverter = ShareCodeConverter(ByteArray(32) { (it + 1).toByte() })
        val joinOrders = (0..8999).toList()

        val shareCodes = joinOrders.map { converter.toShareCode(gatheringId, it) }
        val otherShareCodes = joinOrders.map { otherConverter.toShareCode(gatheringId, it) }

        Assertions.assertThat(otherShareCodes).isNotEqualTo(shareCodes)
    }

    @Test
    fun `joinOrder가 범위를 넘으면 예외가 발생한다`() {
        Assertions.assertThatThrownBy { converter.toShareCode(gatheringId, 9000) }
            .isInstanceOf(CardException::class.java)
            .satisfies({ ex ->
                Assertions.assertThat((ex as CardException).details.code)
                    .isEqualTo(CardExceptionCode.JOIN_CAPACITY_EXCEEDED.name)
            })
    }

    @Test
    fun `shareCode가 범위를 넘으면 예외가 발생한다`() {
        listOf(999, 10000).forEach { shareCode ->
            Assertions.assertThatThrownBy { converter.toJoinOrder(gatheringId, shareCode) }
                .isInstanceOf(CardException::class.java)
                .satisfies({ ex ->
                    Assertions.assertThat((ex as CardException).details.code)
                        .isEqualTo(CardExceptionCode.CARD_NOT_FOUND.name)
                })
        }
    }
}
