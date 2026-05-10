package sharev.account.adapter.outbound.jpa

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import sharev.account.adapter.outbound.jpa.mapper.toDomainModel
import sharev.account.adapter.outbound.jpa.mapper.toJpaEntity
import sharev.account.adapter.outbound.jpa.mapper.updateFrom
import sharev.account.adapter.outbound.jpa.repository.AccountRepository
import sharev.account.application.port.outbound.DeleteAccountPort
import sharev.account.application.port.outbound.LoadAccountPort
import sharev.account.application.port.outbound.SaveAccountPort
import sharev.account.application.port.outbound.UpdateAccountPort
import sharev.account.domain.exception.AccountException
import sharev.account.domain.exception.AccountExceptionCode
import sharev.account.domain.model.Account

@Component
class AccountJpaAdapter(
    private val accountRepository: AccountRepository
) : LoadAccountPort, SaveAccountPort, DeleteAccountPort, UpdateAccountPort {

    override fun load(accountId: Long): Account {
        val accountJpaEntity = accountRepository.findByIdOrNull(accountId)
            ?: throw AccountException(AccountExceptionCode.ACCOUNT_NOT_FOUND)

        return accountJpaEntity.toDomainModel()
    }

    override fun save(account: Account): Account {
        if (account.id == 0L) {
            return accountRepository.save(account.toJpaEntity())
                .toDomainModel()
        }

        val accountJpaEntity = accountRepository.findByIdOrNull(account.id)
            ?.apply { updateFrom(account) }
            ?: throw AccountException(AccountExceptionCode.ACCOUNT_NOT_FOUND)

        return accountRepository.save(accountJpaEntity)
            .toDomainModel()
    }

    override fun update(accountId: Long, name: String, email: String): Account {
        val accountJpaEntity = accountRepository.findByIdOrNull(accountId)
            ?: throw AccountException(AccountExceptionCode.ACCOUNT_NOT_FOUND)

        accountJpaEntity.name = name
        accountJpaEntity.email = email

        return accountJpaEntity.toDomainModel()
    }

    override fun delete(accountId: Long) {
        accountRepository.deleteById(accountId)
    }
}
