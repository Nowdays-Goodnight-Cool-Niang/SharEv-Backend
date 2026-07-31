package sharev.account.adapter.outbound.jpa

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import sharev.account.adapter.outbound.jpa.mapper.toDomainModel
import sharev.account.adapter.outbound.jpa.mapper.toJpaEntity
import sharev.account.adapter.outbound.jpa.mapper.updateFrom
import sharev.account.adapter.outbound.jpa.repository.AccountRepository
import sharev.account.application.port.outbound.*
import sharev.account.domain.exception.AccountException
import sharev.account.domain.exception.AccountExceptionCode
import sharev.account.domain.model.Account
import sharev.common.adapter.outbound.jpa.exception.onUniqueViolation

@Component
class AccountJpaAdapter(
    private val accountRepository: AccountRepository
) : LoadAccountPort,
    SaveAccountPort,
    DeleteAccountPort,
    UpdateAccountPort,
    UpdateAccountHandlePort {

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

    override fun update(accountId: Long, handle: String): Account {
        val accountJpaEntity = (accountRepository.findByIdOrNull(accountId)
            ?: throw AccountException(AccountExceptionCode.ACCOUNT_NOT_FOUND))

        accountJpaEntity.handle = handle

        return onUniqueViolation({ AccountException(AccountExceptionCode.HANDLE_ALREADY_EXISTS) }) {
            accountRepository.saveAndFlush(accountJpaEntity)
                .toDomainModel()
        }
    }
}
