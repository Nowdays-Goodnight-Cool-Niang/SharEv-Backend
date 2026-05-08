package sharev.account.adapter.inbound.web.dto.response

import java.time.LocalDateTime

data class DeleteAccountResponse(
    val id: Long,
    val deletedAt: LocalDateTime,
) {
}
