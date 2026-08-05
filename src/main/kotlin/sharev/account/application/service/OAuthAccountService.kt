package sharev.account.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sharev.account.application.port.inbound.command.OAuthLoginCommand
import sharev.account.application.port.inbound.result.OAuthLoginResult
import sharev.account.application.port.inbound.usecase.OAuthLoginUseCase
import sharev.account.application.port.outbound.LoadAccountPort
import sharev.account.application.port.outbound.LoadOAuthAccountPort
import sharev.account.application.port.outbound.SaveAccountPort
import sharev.account.application.port.outbound.SaveOAuthAccountPort
import sharev.account.domain.event.AccountSignupEvent
import sharev.account.domain.model.Account
import sharev.account.domain.model.AccountRole
import sharev.account.domain.model.OAuthAccount
import sharev.common.application.port.outbound.PublishEventPort

@Service
@Transactional(readOnly = true)
class OAuthAccountService(
    private val loadOAuthAccountPort: LoadOAuthAccountPort,
    private val saveOAuthAccountPort: SaveOAuthAccountPort,
    private val loadAccountPort: LoadAccountPort,
    private val saveAccountPort: SaveAccountPort,
    private val publishEventPort: PublishEventPort,
) : OAuthLoginUseCase {

    @Transactional
    override fun login(command: OAuthLoginCommand): OAuthLoginResult {
        val account = loadOAuthAccountPort.load(command.provider, command.subjectIdentifier)
            ?.let { loadAccountPort.load(it.accountId) }
            ?: signup(command)

        return OAuthLoginResult(
            account.id,
            account.role,
            account.name,
            account.email,
            account.handle,
        )
    }

    private fun signup(command: OAuthLoginCommand): Account {
        val account = saveAccountPort.save(Account(0L, command.name, command.email, AccountRole.USER, null))
        saveOAuthAccountPort.save(OAuthAccount(command.provider, command.subjectIdentifier, account.id))
        publishEventPort.publish(AccountSignupEvent(account.id))
        return account
    }
}
