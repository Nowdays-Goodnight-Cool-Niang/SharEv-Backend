package sharev.link.application.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import sharev.link.application.port.inbound.command.GetLinksCommand
import sharev.link.application.port.outbound.LoadLinkPort
import sharev.link.domain.model.Link

class LinkServiceTest {
    private val loadLinkPort = mock(LoadLinkPort::class.java)

    private val linkService = LinkService(
        loadLinkPort,
    )

    // ───────────── getLinks ─────────────

    @Test
    @DisplayName("정상 조회 시 getLinks는 링크 목록을 반환한다")
    fun getLinks_returnsLinkList() {
        val accountId = 10L
        val command = GetLinksCommand(accountId = accountId)
        val links = listOf(
            link(id = 1L, accountId = accountId, url = "https://example.com"),
            link(id = 2L, accountId = accountId, url = "https://another.com"),
        )

        given(loadLinkPort.loadAllByAccountId(accountId)).willReturn(links)

        val result = linkService.getLinks(command)

        assertThat(result).hasSize(2)
        assertThat(result[0].id).isEqualTo(1L)
        assertThat(result[1].id).isEqualTo(2L)
    }

    // ───────────── helpers ─────────────

    private fun link(
        id: Long,
        accountId: Long,
        url: String,
    ) = Link(
        id = id,
        accountId = accountId,
        url = url,
    )
}
