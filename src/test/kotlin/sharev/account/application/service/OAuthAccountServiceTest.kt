package sharev.account.application.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import sharev.account.application.port.inbound.command.OAuthLoginCommand
import sharev.account.application.port.outbound.LoadAccountPort
import sharev.account.application.port.outbound.LoadOAuthAccountPort
import sharev.account.application.port.outbound.SaveAccountPort
import sharev.account.application.port.outbound.SaveOAuthAccountPort
import sharev.account.domain.model.Account
import sharev.account.domain.model.AccountRole
import sharev.account.domain.model.OAuthAccount
import sharev.account.domain.model.OAuthProvider

class OAuthAccountServiceTest {
    private val loadOAuthAccountPort = mock(LoadOAuthAccountPort::class.java)
    private val saveOAuthAccountPort = mock(SaveOAuthAccountPort::class.java)
    private val loadAccountPort = mock(LoadAccountPort::class.java)
    private val saveAccountPort = mock(SaveAccountPort::class.java)

    private val oAuthAccountService = OAuthAccountService(
        loadOAuthAccountPort,
        saveOAuthAccountPort,
        loadAccountPort,
        saveAccountPort,
    )

    // ───────────── login ─────────────

    @Test
    fun `기존 OAuth 계정이면 login 시 기존 account를 반환한다`() {
        val accountId = 10L
        val provider = OAuthProvider.KAKAO
        val subjectIdentifier = "kakao-subject-001"
        // command의 name/email은 DB에 저장된 것과 다르게 설정 — 서비스가 DB에서 로드함을 증명
        val command = OAuthLoginCommand(
            provider = provider,
            subjectIdentifier = subjectIdentifier,
            name = "새이름",
            email = "new@test.com",
        )
        val existingOAuthAccount = OAuthAccount(
            provider = provider,
            subjectIdentifier = subjectIdentifier,
            accountId = accountId,
        )
        val existingAccount = account(id = accountId, name = "홍길동", email = "hong@test.com")

        given(loadOAuthAccountPort.load(provider, subjectIdentifier)).willReturn(existingOAuthAccount)
        given(loadAccountPort.load(accountId)).willReturn(existingAccount)

        val result = oAuthAccountService.login(command)

        // result는 command가 아닌 기존 DB account의 name/email을 반환해야 함
        assertThat(result.id).isEqualTo(accountId)
        assertThat(result.name).isEqualTo("홍길동")
        assertThat(result.email).isEqualTo("hong@test.com")
        then(saveAccountPort).shouldHaveNoInteractions()
        then(saveOAuthAccountPort).shouldHaveNoInteractions()
    }

    @Test
    fun `신규 사용자면 login 시 account를 저장하고 OAuthAccount를 저장한다`() {
        val provider = OAuthProvider.KAKAO
        val subjectIdentifier = "kakao-subject-new"
        val command = OAuthLoginCommand(
            provider = provider,
            subjectIdentifier = subjectIdentifier,
            name = "신규유저",
            email = "new@test.com",
        )
        val savedAccount = account(id = 99L, name = command.name, email = command.email)
        val savedOAuthAccount = OAuthAccount(
            provider = provider,
            subjectIdentifier = subjectIdentifier,
            accountId = savedAccount.id,
        )
        val accountCaptor = argumentCaptor<Account>()
        val oAuthAccountCaptor = argumentCaptor<OAuthAccount>()

        given(loadOAuthAccountPort.load(provider, subjectIdentifier)).willReturn(null)
        given(saveAccountPort.save(any())).willReturn(savedAccount)
        given(saveOAuthAccountPort.save(any())).willReturn(savedOAuthAccount)

        val result = oAuthAccountService.login(command)

        then(saveAccountPort).should().save(accountCaptor.capture())
        val capturedAccount = accountCaptor.firstValue
        assertThat(capturedAccount.id).isEqualTo(0L)
        assertThat(capturedAccount.name).isEqualTo(command.name)
        assertThat(capturedAccount.email).isEqualTo(command.email)
        assertThat(capturedAccount.role).isEqualTo(AccountRole.USER)

        then(saveOAuthAccountPort).should().save(oAuthAccountCaptor.capture())
        val capturedOAuthAccount = oAuthAccountCaptor.firstValue
        assertThat(capturedOAuthAccount.provider).isEqualTo(command.provider)
        assertThat(capturedOAuthAccount.subjectIdentifier).isEqualTo(command.subjectIdentifier)
        assertThat(capturedOAuthAccount.accountId).isEqualTo(savedAccount.id)

        assertThat(result.id).isEqualTo(savedAccount.id)
        assertThat(result.name).isEqualTo(command.name)
        assertThat(result.email).isEqualTo(command.email)
        then(loadAccountPort).shouldHaveNoInteractions()
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
