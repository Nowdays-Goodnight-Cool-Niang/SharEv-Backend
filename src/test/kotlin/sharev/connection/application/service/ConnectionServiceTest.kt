package sharev.connection.application.service

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock
import sharev.card.application.port.outbound.LoadCardPort
import sharev.card.domain.exception.CardException
import sharev.card.domain.model.Card
import sharev.connection.application.port.inbound.command.ConnectCardsCommand
import sharev.connection.application.port.outbound.SaveConnectionPort
import java.util.UUID

class ConnectionServiceTest {
    private val loadCardPort = mock(LoadCardPort::class.java)
    private val saveConnectionPort = mock(SaveConnectionPort::class.java)
    private val connectionService = ConnectionService(loadCardPort, saveConnectionPort)

    @Test
    fun `같은 행사에 속하지 않은 카드는 연결할 수 없다`() {
        val gatheringId = UUID.randomUUID()
        val otherGatheringId = UUID.randomUUID()
        val myCard = card(id = 1L, gatheringId = gatheringId, accountId = 1L)
        val targetCard = card(id = 2L, gatheringId = otherGatheringId, accountId = 2L)

        given(loadCardPort.loadByGatheringAndAccount(gatheringId, 1L)).willReturn(myCard)
        given(loadCardPort.load(2L)).willReturn(targetCard)

        assertThatThrownBy { connectionService.connect(ConnectCardsCommand(gatheringId, 1L, 2L)) }
            .isInstanceOf(CardException::class.java)

        then(saveConnectionPort).shouldHaveNoInteractions()
    }

    private fun card(
        id: Long,
        gatheringId: UUID,
        accountId: Long,
    ) = Card(
        id = id,
        gatheringId = gatheringId,
        accountId = accountId,
        accountName = "name-$accountId",
        accountEmail = "account$accountId@test.com",
        pinNumber = 1234,
        templateVersion = 1,
        introductionText = mapOf("name" to "value"),
    )
}
