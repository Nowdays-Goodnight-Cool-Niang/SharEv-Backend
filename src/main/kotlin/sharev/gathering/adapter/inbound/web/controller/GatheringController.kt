package sharev.gathering.adapter.inbound.web.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import sharev.common.adapter.inbound.security.model.AccountPrincipal
import sharev.gathering.adapter.inbound.web.dto.request.CreateGatheringRequest
import sharev.gathering.adapter.inbound.web.dto.request.GetGatheringRequest
import sharev.gathering.adapter.inbound.web.dto.request.UpdateGatheringRequest
import sharev.gathering.adapter.inbound.web.dto.request.UpsertIntroductionRequest
import sharev.gathering.adapter.inbound.web.dto.response.*
import sharev.gathering.adapter.inbound.web.mapper.toCommand
import sharev.gathering.adapter.inbound.web.mapper.toResponse
import sharev.gathering.application.port.inbound.usecase.*
import java.util.*

@RestController("/gatherings")
class GatheringController(
    private val createGatheringUseCase: CreateGatheringUseCase,
    private val updateGatheringUseCase: UpdateGatheringUseCase,
    private val deleteGatheringUseCase: DeleteGatheringUseCase,
    private val getIntroductionUseCase: GetIntroductionUseCase,
    private val checkGatheringParticipantUseCase: CheckGatheringParticipantUseCase,
    private val getGatheringsUseCase: GetGatheringsUseCase,
    private val getGatheringUseCase: GetGatheringUseCase,
    private val upsertIntroductionUseCase: UpsertIntroductionUseCase,
) {

    @GetMapping
    fun allGatherings(
        @ModelAttribute getGatheringRequest: GetGatheringRequest,
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal?,
        @PageableDefault(size = 20) pageable: Pageable,
    ): ResponseEntity<Page<GatheringDetailResponse>> {
        return ResponseEntity.ok(
            getGatheringsUseCase.getGatherings(
                getGatheringRequest.toCommand(accountPrincipal?.id),
                pageable
            ).map { it.toResponse() }
        )
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/managed")
    fun getManagedGatherings(
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        @PageableDefault(size = 20) pageable: Pageable,
    ): ResponseEntity<Page<GatheringDetailResponse>> {
        return ResponseEntity.ok(
            getGatheringsUseCase.getManagedGatherings(accountPrincipal.id, pageable)
                .map { it.toResponse() }
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

    @PostMapping
    fun createGathering(
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        @Valid @RequestBody request: CreateGatheringRequest,
    ): ResponseEntity<CreateGatheringResponse> {
        val response = createGatheringUseCase.create(
            request.toCommand(accountPrincipal.id)
        ).toResponse()

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(response)
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

    @PatchMapping("/{gatheringId}")
    fun updateGathering(
        @PathVariable gatheringId: UUID,
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        @Valid @RequestBody request: UpdateGatheringRequest,
    ): ResponseEntity<GatheringDetailResponse> {
        return ResponseEntity.ok(
            updateGatheringUseCase.update(
                request.toCommand(
                    accountPrincipal.id,
                    gatheringId
                )
            ).toResponse()
        )
    }

    @DeleteMapping("/{gatheringId}")
    fun deleteGathering(
        @PathVariable gatheringId: UUID,
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
    ): ResponseEntity<DeleteGatheringResponse> {
        val response = deleteGatheringUseCase.delete(
            accountPrincipal.id, gatheringId
        ).toResponse()
        return ResponseEntity.ok(response)
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

    @PutMapping("/{gatheringId}/introduction")
    fun upsertIntroduction(
        @PathVariable gatheringId: UUID,
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        @RequestBody request: UpsertIntroductionRequest,
    ): ResponseEntity<IntroductionResponse> {
        return ResponseEntity.ok(
            upsertIntroductionUseCase.upsertIntroduction(
                request.toCommand(accountPrincipal.id, gatheringId)
            ).toResponse()
        )
    }
}
