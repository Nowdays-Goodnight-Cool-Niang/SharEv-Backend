package sharev.account.application.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import sharev.account.application.port.inbound.command.DeleteAccountCommand
import sharev.account.application.port.inbound.command.UpdateAccountInfoCommand
import sharev.account.application.port.outbound.DeleteAccountPort
import sharev.account.application.port.outbound.UpdateAccountPort
import sharev.account.domain.event.AccountLinkUpdatedEvent
import sharev.account.domain.event.AccountWithdrawalFeedbackSubmittedEvent
import sharev.account.domain.model.Account
import sharev.account.domain.model.AccountRole
import sharev.common.application.port.outbound.PublishEventPort

class AccountServiceTest {
    private val updateAccountPort = mock(UpdateAccountPort::class.java)
    private val deleteAccountPort = mock(DeleteAccountPort::class.java)
    private val publishEventPort = mock(PublishEventPort::class.java)

    private val accountService = AccountService(
        updateAccountPort,
        deleteAccountPort,
        publishEventPort,
    )

    // ───────────── updateAccountInfo ─────────────

    @Test
    @DisplayName("정상 수정 시 updateAccountInfo는 업데이트된 계정 정보를 반환한다")
    fun updateAccountInfo_returnsUpdatedInfo() {
        val accountId = 10L
        val command = UpdateAccountInfoCommand(
            accountId = accountId,
            name = "새이름",
            email = "new@test.com",
            addLinkUrls = setOf("https://new-link.com"),
            deleteLinkIds = setOf(1L, 2L),
        )
        val updatedAccount = account(id = accountId, name = "새이름", email = "new@test.com")

        given(updateAccountPort.update(accountId, "새이름", "new@test.com")).willReturn(updatedAccount)

        val result = accountService.updateAccountInfo(command)

        assertThat(result.id).isEqualTo(accountId)
        assertThat(result.name).isEqualTo("새이름")
        assertThat(result.email).isEqualTo("new@test.com")
        then(updateAccountPort).should().update(accountId, "새이름", "new@test.com")
    }

    @Test
    @DisplayName("updateAccountInfo는 링크 변경 이벤트를 발행한다")
    fun updateAccountInfo_publishesLinkUpdatedEvent() {
        val accountId = 10L
        val addLinkUrls = setOf("https://link1.com", "https://link2.com")
        val deleteLinkIds = setOf(3L, 4L)
        val command = UpdateAccountInfoCommand(
            accountId = accountId,
            name = "이름",
            email = "email@test.com",
            addLinkUrls = addLinkUrls,
            deleteLinkIds = deleteLinkIds,
        )
        val updatedAccount = account(id = accountId, name = "이름", email = "email@test.com")
        val eventCaptor = argumentCaptor<Any>()

        given(updateAccountPort.update(accountId, "이름", "email@test.com")).willReturn(updatedAccount)

        accountService.updateAccountInfo(command)

        then(publishEventPort).should().publish(eventCaptor.capture())
        val capturedEvent = eventCaptor.firstValue as AccountLinkUpdatedEvent
        assertThat(capturedEvent.accountId).isEqualTo(accountId)
        assertThat(capturedEvent.addLinkUrls).isEqualTo(addLinkUrls)
        assertThat(capturedEvent.deleteLinkIds).isEqualTo(deleteLinkIds)
    }

    // ───────────── delete ─────────────

    @Test
    @DisplayName("피드백이 blank이면 delete 시 이벤트를 발행하지 않는다")
    fun delete_doesNotPublishEvent_whenFeedbackIsBlank() {
        val accountId = 10L
        val command = DeleteAccountCommand(accountId = accountId, feedback = "  ")

        val result = accountService.delete(command)

        assertThat(result.id).isEqualTo(accountId)
        then(deleteAccountPort).should().delete(accountId)
        then(publishEventPort).should(never()).publish(any())
    }

    @Test
    @DisplayName("피드백이 non-blank이면 delete 시 이벤트를 발행한다")
    fun delete_publishesFeedbackEvent_whenFeedbackIsNonBlank() {
        val accountId = 10L
        val feedback = "서비스가 불편했습니다"
        val command = DeleteAccountCommand(accountId = accountId, feedback = feedback)
        val eventCaptor = argumentCaptor<Any>()

        val result = accountService.delete(command)

        assertThat(result.id).isEqualTo(accountId)
        then(deleteAccountPort).should().delete(accountId)
        then(publishEventPort).should().publish(eventCaptor.capture())
        val capturedEvent = eventCaptor.firstValue
        assertThat(capturedEvent).isInstanceOf(AccountWithdrawalFeedbackSubmittedEvent::class.java)
        assertThat((capturedEvent as AccountWithdrawalFeedbackSubmittedEvent).feedback).isEqualTo(command.feedback)
    }

    @Test
    @DisplayName("정상 삭제 시 delete는 deleteAccountPort를 호출하고 accountId를 반환한다")
    fun delete_callsDeletePortAndReturnsAccountId() {
        val accountId = 10L
        val command = DeleteAccountCommand(accountId = accountId, feedback = "")

        val result = accountService.delete(command)

        assertThat(result.id).isEqualTo(accountId)
        then(deleteAccountPort).should().delete(accountId)
    }

    // ───────────── helpers ─────────────

    private fun account(
        id: Long,
        name: String = "name",
        email: String = "account@test.com",
        role: AccountRole = AccountRole.USER,
    ) = Account(
        id = id,
        name = name,
        email = email,
        role = role,
    )
}
