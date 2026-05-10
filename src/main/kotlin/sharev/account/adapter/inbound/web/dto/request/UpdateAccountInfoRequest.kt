package sharev.account.adapter.inbound.web.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class UpdateAccountInfoRequest(
    @field:NotBlank
    @field:Size(max = 255)
    val name: String,

    @field:Email
    @field:Size(max = 320)
    val email: String,

    @field:NotNull
    val addLinkUrls: Set<String>? = emptySet(),

    @field:NotNull
    val deleteLinkIds: Set<Long>? = emptySet(),
)
