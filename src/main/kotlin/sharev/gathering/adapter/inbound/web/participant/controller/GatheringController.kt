package sharev.gathering.adapter.inbound.web.participant.controller

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import sharev.common.adapter.inbound.security.model.AccountPrincipal
import sharev.gathering.adapter.inbound.web.participant.dto.request.GetGatheringRequest
import sharev.gathering.adapter.inbound.web.participant.dto.response.GatheringDetailResponse
import sharev.gathering.adapter.inbound.web.participant.dto.response.GatheringResponse
import sharev.gathering.adapter.inbound.web.participant.dto.response.IntroductionResponse
import sharev.gathering.adapter.inbound.web.participant.dto.response.ParticipantResponse
import sharev.gathering.adapter.inbound.web.participant.mapper.toCommand
import sharev.gathering.adapter.inbound.web.participant.mapper.toResponse
import sharev.gathering.application.port.inbound.usecase.CheckGatheringParticipantUseCase
import sharev.gathering.application.port.inbound.usecase.GetGatheringUseCase
import sharev.gathering.application.port.inbound.usecase.GetGatheringsUseCase
import sharev.gathering.application.port.inbound.usecase.GetIntroductionUseCase
import java.util.*

@RestController
@RequestMapping("/gatherings")
class GatheringController(
    private val getIntroductionUseCase: GetIntroductionUseCase,
    private val checkGatheringParticipantUseCase: CheckGatheringParticipantUseCase,
    private val getGatheringsUseCase: GetGatheringsUseCase,
    private val getGatheringUseCase: GetGatheringUseCase,
) {

    @GetMapping
    fun allGatherings(
        @ModelAttribute getGatheringRequest: GetGatheringRequest,
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal?,
        @PageableDefault(size = 20) pageable: Pageable,
    ): ResponseEntity<Page<GatheringResponse>> {
        return ResponseEntity.ok(
            getGatheringsUseCase.getGatherings(
                getGatheringRequest.toCommand(accountPrincipal?.id),
                pageable
            ).map { it.toResponse() }
        )
    }

    @GetMapping("/{gatheringId}/participant")
    fun isParticipant(
        @PathVariable gatheringId: UUID,
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
    ): ResponseEntity<ParticipantResponse> {
        return ResponseEntity.ok(
            checkGatheringParticipantUseCase.isParticipant(accountPrincipal.id, gatheringId)
                .toResponse()
        )
    }

    @GetMapping("/{gatheringId}")
    fun getGathering(
        @PathVariable gatheringId: UUID,
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal?,
    ): ResponseEntity<GatheringDetailResponse> {
        return ResponseEntity.ok(
            getGatheringUseCase.getGathering(
                accountPrincipal?.id, gatheringId
            ).toResponse()
        )
    }

    @GetMapping("/{gatheringId}/introduction")
    fun getIntroduction(
        @PathVariable gatheringId: UUID,
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
    ): ResponseEntity<IntroductionResponse> {
        return ResponseEntity.ok(
            getIntroductionUseCase.getLatestIntroduction(
                gatheringId, accountPrincipal.id
            ).toResponse()
        )
    }
}
