package sharev.link.adapter.outbound.jpa

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import sharev.account.adapter.outbound.jpa.repository.AccountRepository
import sharev.account.domain.exception.AccountException
import sharev.account.domain.exception.AccountExceptionCode
import sharev.link.adapter.outbound.jpa.entity.LinkJpaEntity
import sharev.link.adapter.outbound.jpa.mapper.toDomainModel
import sharev.link.adapter.outbound.jpa.repository.LinkRepository
import sharev.link.application.port.outbound.DeleteLinkPort
import sharev.link.application.port.outbound.LoadLinkPort
import sharev.link.application.port.outbound.SaveLinkPort
import sharev.link.domain.exception.LinkException
import sharev.link.domain.exception.LinkExceptionCode
import sharev.link.domain.model.Link

@Component
class LinkJpaAdapter(
    private val linkRepository: LinkRepository,
    private val accountRepository: AccountRepository,
) : SaveLinkPort, LoadLinkPort, DeleteLinkPort {

    override fun save(accountId: Long, url: String): Link {
        val account = accountRepository.findByIdOrNull(accountId)
            ?: throw AccountException(AccountExceptionCode.ACCOUNT_NOT_FOUND)

        return linkRepository.save(LinkJpaEntity(account = account, linkUrl = url))
            .toDomainModel()
    }

    override fun saveAll(accountId: Long, urls: Set<String>) {
        val account = accountRepository.findByIdOrNull(accountId)
            ?: throw AccountException(AccountExceptionCode.ACCOUNT_NOT_FOUND)

        val currentCount = linkRepository.countByAccountId(accountId)

        if (currentCount + urls.size > Link.MAX_COUNT) {
            throw LinkException(LinkExceptionCode.LINK_LIMIT_EXCEEDED)
        }

        val entities = urls.map { LinkJpaEntity(account = account, linkUrl = it) }
        linkRepository.saveAll(entities)
    }

    override fun load(linkId: Long): Link {
        return linkRepository.findByIdOrNull(linkId)
            ?.toDomainModel()
            ?: throw LinkException(LinkExceptionCode.LINK_NOT_FOUND)
    }

    override fun loadAllByAccountId(accountId: Long): List<Link> {
        return linkRepository.findAllByAccountId(accountId)
            .map { it.toDomainModel() }
    }

    override fun loadAllByAccountIdIn(accountIds: Collection<Long>): List<Link> {
        return linkRepository.findAllByAccountIdIn(accountIds)
            .map { it.toDomainModel() }
    }

    override fun delete(linkId: Long) {
        linkRepository.deleteById(linkId)
    }

    override fun deleteAllByIds(accountId: Long, linkIds: Set<Long>) {
        val links = linkRepository.findAllById(linkIds)

        val hasOwnershipMismatch = links.any { it.account.id != accountId }

        if (hasOwnershipMismatch) {
            throw LinkException(LinkExceptionCode.LINK_OWNERSHIP_MISMATCH)
        }

        linkRepository.deleteAllInBatch(links)
    }
}
