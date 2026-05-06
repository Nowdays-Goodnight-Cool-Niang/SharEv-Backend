package sharev.account.adapter.inbound.web.dto.request

import jakarta.validation.constraints.Size

data class DeleteAccountRequest(
    @field:Size(max = 500)
    val feedback: String
) {
}
