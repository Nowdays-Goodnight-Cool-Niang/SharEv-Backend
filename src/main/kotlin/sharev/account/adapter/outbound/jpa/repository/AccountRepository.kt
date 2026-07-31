package sharev.account.adapter.outbound.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository
import sharev.account.adapter.outbound.jpa.entity.AccountJpaEntity
import java.util.*

interface AccountRepository : JpaRepository<AccountJpaEntity, Long> {
    fun findByEmail(email: String): Optional<AccountJpaEntity>

    fun existsByHandleAndIdNot(handle: String, id: Long): Boolean
}
