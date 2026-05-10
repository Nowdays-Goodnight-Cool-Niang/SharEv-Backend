package sharev.link.adapter.inbound.event

import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import sharev.account.domain.event.AccountLinkUpdatedEvent
import sharev.link.application.port.outbound.DeleteLinkPort
import sharev.link.application.port.outbound.SaveLinkPort

class AccountLinkUpdatedEventListenerTest {
    private val saveLinkPort = mock(SaveLinkPort::class.java)
    private val deleteLinkPort = mock(DeleteLinkPort::class.java)

    private val listener = AccountLinkUpdatedEventListener(saveLinkPort, deleteLinkPort)

    @Test
    fun `추가 링크가 있으면 saveAll을 호출한다`() {
        val accountId = 10L
        val addLinkUrls = setOf("https://link1.com", "https://link2.com")
        val event = AccountLinkUpdatedEvent(
            accountId = accountId,
            addLinkUrls = addLinkUrls,
            deleteLinkIds = emptySet(),
        )

        listener.handle(event)

        then(saveLinkPort).should().saveAll(accountId, addLinkUrls)
        then(deleteLinkPort).should(never()).deleteAllByIds(any(), any())
    }

    @Test
    fun `삭제 링크가 있으면 deleteAllByIds를 호출한다`() {
        val accountId = 10L
        val deleteLinkIds = setOf(1L, 2L)
        val event = AccountLinkUpdatedEvent(
            accountId = accountId,
            addLinkUrls = emptySet(),
            deleteLinkIds = deleteLinkIds,
        )

        listener.handle(event)

        then(saveLinkPort).should(never()).saveAll(any(), any())
        then(deleteLinkPort).should().deleteAllByIds(accountId, deleteLinkIds)
    }

    @Test
    fun `추가와 삭제 링크가 모두 있으면 saveAll과 deleteAllByIds를 모두 호출한다`() {
        val accountId = 10L
        val addLinkUrls = setOf("https://new.com")
        val deleteLinkIds = setOf(5L)
        val event = AccountLinkUpdatedEvent(
            accountId = accountId,
            addLinkUrls = addLinkUrls,
            deleteLinkIds = deleteLinkIds,
        )

        listener.handle(event)

        then(saveLinkPort).should().saveAll(accountId, addLinkUrls)
        then(deleteLinkPort).should().deleteAllByIds(accountId, deleteLinkIds)
    }

    @Test
    fun `추가와 삭제 링크가 모두 비어있으면 아무 포트도 호출하지 않는다`() {
        val event = AccountLinkUpdatedEvent(
            accountId = 10L,
            addLinkUrls = emptySet(),
            deleteLinkIds = emptySet(),
        )

        listener.handle(event)

        then(saveLinkPort).should(never()).saveAll(any(), any())
        then(deleteLinkPort).should(never()).deleteAllByIds(any(), any())
    }
}
