package sharev.account.application.port.inbound.result

import sharev.account.domain.model.AccountRole
import java.time.LocalDateTime

data class UpdateAccountInfoResult(
    val id: Long,
    val name: String,
    val email: String,
    val role: AccountRole,
    val updateAt: LocalDateTime = LocalDateTime.now()
) {
}
