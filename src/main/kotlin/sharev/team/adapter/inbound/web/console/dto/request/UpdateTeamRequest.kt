package sharev.team.adapter.inbound.web.console.dto.request

import jakarta.validation.constraints.NotBlank

data class UpdateTeamRequest(
    @field:NotBlank
    val title: String?,

    val content: String?,
)
