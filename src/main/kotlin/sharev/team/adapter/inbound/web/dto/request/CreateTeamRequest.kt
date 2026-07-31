package sharev.team.adapter.inbound.web.dto.request

import jakarta.validation.constraints.NotBlank

data class CreateTeamRequest(
    @field:NotBlank
    val title: String,

    @field:NotBlank
    val content: String,
)
