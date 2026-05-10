package sharev.account.application.port.inbound.result

import java.time.LocalDateTime

data class UpdateAccountInfoResult(
    val id: Long,
    val name: String,
    val email: String,
    val updateAt: LocalDateTime = LocalDateTime.now()
) {
}
