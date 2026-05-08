package sharev.link.adapter.outbound.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository
import sharev.link.adapter.outbound.jpa.entity.LinkJpaEntity

interface LinkRepository : JpaRepository<LinkJpaEntity, Long> {
    fun findAllByAccountIdIn(accountIds: Collection<Long>): List<LinkJpaEntity>

    fun findAllByAccountId(accountId: Long): List<LinkJpaEntity>
}
