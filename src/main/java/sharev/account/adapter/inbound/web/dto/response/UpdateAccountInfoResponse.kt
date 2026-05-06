package sharev.account.adapter.inbound.web.dto.response

import java.time.LocalDateTime

data class UpdateAccountInfoResponse(
    val id: Long,
    val name: String,
    val email: String,
    val updatedAt: LocalDateTime,
) {
}
