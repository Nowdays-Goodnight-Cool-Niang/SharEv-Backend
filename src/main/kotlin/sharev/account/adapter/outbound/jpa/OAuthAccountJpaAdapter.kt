package sharev.account.adapter.outbound.jpa

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import sharev.account.adapter.outbound.jpa.entity.OAuthAccountJpaEntityId
import sharev.account.adapter.outbound.jpa.mapper.toDomainModel
import sharev.account.adapter.outbound.jpa.mapper.toJpaEntity
import sharev.account.adapter.outbound.jpa.repository.AccountRepository
import sharev.account.adapter.outbound.jpa.repository.OAuthAccountRepository
import sharev.account.application.port.outbound.LoadOAuthAccountPort
import sharev.account.application.port.outbound.SaveOAuthAccountPort
import sharev.account.domain.exception.AccountException
import sharev.account.domain.exception.AccountExceptionCode
import sharev.account.domain.model.OAuthAccount
import sharev.account.domain.model.OAuthProvider

@Component
class OAuthAccountJpaAdapter(
    val oAuthAccountRepository: OAuthAccountRepository,
    val accountRepository: AccountRepository,
) : LoadOAuthAccountPort, SaveOAuthAccountPort {

    override fun load(provider: OAuthProvider, subjectIdentifier: String): OAuthAccount? {
        return oAuthAccountRepository.findByIdOrNull(
            OAuthAccountJpaEntityId(provider, subjectIdentifier)
        )?.toDomainModel()
    }

    override fun save(oAuthAccount: OAuthAccount): OAuthAccount {
        val accountJpaEntity = accountRepository.findByIdOrNull(oAuthAccount.accountId)
            ?: throw AccountException(AccountExceptionCode.ACCOUNT_SAVE_FAILED)

        return oAuthAccountRepository.save(oAuthAccount.toJpaEntity(accountJpaEntity))
            .toDomainModel()
    }
}
