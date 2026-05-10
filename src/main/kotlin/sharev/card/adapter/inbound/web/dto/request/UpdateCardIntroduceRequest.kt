package sharev.card.adapter.inbound.web.dto.request

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class UpdateCardIntroduceRequest(
    @field:NotNull
    @field:Positive
    val version: Int?,

    @field:NotNull
    val introductionText: Map<String, String>?,
)
