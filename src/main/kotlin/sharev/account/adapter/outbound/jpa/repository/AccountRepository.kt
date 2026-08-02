package sharev.account.adapter.outbound.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository
import sharev.account.adapter.outbound.jpa.entity.AccountJpaEntity

interface AccountRepository : JpaRepository<AccountJpaEntity, Long> {
    fun findByHandle(handle: String): AccountJpaEntity?

    fun existsByHandleAndIdNot(handle: String, id: Long): Boolean
}
