package sharev.link.application.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import sharev.link.application.port.inbound.command.CreateLinkCommand
import sharev.link.application.port.inbound.command.DeleteLinkCommand
import sharev.link.application.port.inbound.command.GetLinksCommand
import sharev.link.application.port.outbound.DeleteLinkPort
import sharev.link.application.port.outbound.LoadLinkPort
import sharev.link.application.port.outbound.SaveLinkPort
import sharev.link.domain.exception.LinkException
import sharev.link.domain.exception.LinkExceptionCode
import sharev.link.domain.model.Link

class LinkServiceTest {
    private val saveLinkPort = mock(SaveLinkPort::class.java)
    private val loadLinkPort = mock(LoadLinkPort::class.java)
    private val deleteLinkPort = mock(DeleteLinkPort::class.java)

    private val linkService = LinkService(
        saveLinkPort,
        loadLinkPort,
        deleteLinkPort,
    )

    // ───────────── create ─────────────

    @Test
    @DisplayName("정상 생성 시 create는 저장된 링크를 반환한다")
    fun create_returnsSavedLink() {
        val accountId = 10L
        val url = "https://example.com"
        val command = CreateLinkCommand(accountId = accountId, url = url)
        val savedLink = link(id = 1L, accountId = accountId, url = url)

        given(saveLinkPort.save(accountId, url)).willReturn(savedLink)

        val result = linkService.create(command)

        assertThat(result.id).isEqualTo(1L)
        assertThat(result.url).isEqualTo(url)
        then(saveLinkPort).should().save(accountId, url)
    }

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

    // ───────────── delete ─────────────

    @Test
    @DisplayName("소유자 불일치 시 delete는 LINK_OWNERSHIP_MISMATCH 예외가 발생한다")
    fun delete_throwsOwnershipMismatch_whenNotOwner() {
        val accountId = 10L
        val otherAccountId = 99L
        val linkId = 1L
        val command = DeleteLinkCommand(accountId = accountId, linkId = linkId)
        val linkOwnedByOther = link(id = linkId, accountId = otherAccountId, url = "https://example.com")

        given(loadLinkPort.load(linkId)).willReturn(linkOwnedByOther)

        assertThatThrownBy { linkService.delete(command) }
            .isInstanceOf(LinkException::class.java)
            .satisfies({ ex ->
                val linkEx = ex as LinkException
                assertThat(linkEx.details.code).isEqualTo(LinkExceptionCode.LINK_OWNERSHIP_MISMATCH.name)
            })

        then(deleteLinkPort).should(never()).delete(org.mockito.ArgumentMatchers.anyLong())
    }

    @Test
    @DisplayName("정상 삭제 시 delete는 deleteLinkPort를 호출하고 linkId를 반환한다")
    fun delete_callsDeletePortAndReturnsLinkId() {
        val accountId = 10L
        val linkId = 1L
        val command = DeleteLinkCommand(accountId = accountId, linkId = linkId)
        val ownedLink = link(id = linkId, accountId = accountId, url = "https://example.com")

        given(loadLinkPort.load(linkId)).willReturn(ownedLink)

        val result = linkService.delete(command)

        assertThat(result.linkId).isEqualTo(linkId)
        then(deleteLinkPort).should().delete(linkId)
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
