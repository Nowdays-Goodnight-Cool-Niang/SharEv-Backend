package sharev.team.adapter.inbound.web.dto.request

import jakarta.validation.constraints.NotBlank

data class UpdateTeamRequest(
    @field:NotBlank
    val title: String?,

    val content: String?,
)
