package sharev.account.application.port.inbound.result

import java.time.LocalDateTime

data class DeleteAccountResult(
    val id: Long,
    val deleteAt: LocalDateTime = LocalDateTime.now(),
) {
}
