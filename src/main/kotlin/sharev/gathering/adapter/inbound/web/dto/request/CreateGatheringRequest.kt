package sharev.gathering.adapter.inbound.web.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import sharev.gathering.domain.model.GatheringVisible
import java.time.LocalDateTime

data class CreateGatheringRequest(
    @field:NotNull
    val visible: GatheringVisible?,

    @field:NotBlank
    val title: String?,

    @field:NotBlank
    val content: String?,

    @field:NotNull
    val startAt: LocalDateTime?,

    @field:NotNull
    val endAt: LocalDateTime?,

    @field:NotBlank
    val place: String?,

    val imageUrl: String?,

    val gatheringUrl: String?,

    val contact: String?,

    @field:NotNull
    val registerStartAt: LocalDateTime?,

    @field:NotNull
    val registerEndAt: LocalDateTime?,
)


