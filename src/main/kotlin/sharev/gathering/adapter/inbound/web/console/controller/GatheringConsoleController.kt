package sharev.gathering.adapter.inbound.web.console.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import sharev.common.adapter.inbound.security.model.AccountPrincipal
import sharev.gathering.adapter.inbound.web.console.dto.request.CreateGatheringRequest
import sharev.gathering.adapter.inbound.web.console.dto.request.UpdateGatheringRequest
import sharev.gathering.adapter.inbound.web.console.dto.request.UpsertIntroductionRequest
import sharev.gathering.adapter.inbound.web.console.dto.response.CreateGatheringResponse
import sharev.gathering.adapter.inbound.web.console.dto.response.DeleteGatheringResponse
import sharev.gathering.adapter.inbound.web.console.dto.response.ManagedGatheringDetailResponse
import sharev.gathering.adapter.inbound.web.console.dto.response.UpsertIntroductionResponse
import sharev.gathering.adapter.inbound.web.console.mapper.toCommand
import sharev.gathering.adapter.inbound.web.console.mapper.toResponse
import sharev.gathering.application.port.inbound.usecase.*
import java.util.*

@RestController
@RequestMapping("/console/gatherings")
class GatheringConsoleController(
    private val getManagedGatheringsUseCase: GetManagedGatheringsUseCase,
    private val createGatheringUseCase: CreateGatheringUseCase,
    private val updateGatheringUseCase: UpdateGatheringUseCase,
    private val deleteGatheringUseCase: DeleteGatheringUseCase,
    private val upsertIntroductionUseCase: UpsertIntroductionUseCase,
) {

    @GetMapping
    fun getManagedGatherings(
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        @PageableDefault(size = 20) pageable: Pageable,
    ): ResponseEntity<Page<ManagedGatheringDetailResponse>> {
        return ResponseEntity.ok(
            getManagedGatheringsUseCase.getManagedGatherings(accountPrincipal.id, pageable)
                .map { it.toResponse() }
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

    @PatchMapping("/{gatheringId}")
    fun updateGathering(
        @PathVariable gatheringId: UUID,
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        @Valid @RequestBody request: UpdateGatheringRequest,
    ): ResponseEntity<ManagedGatheringDetailResponse> {
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

    @PutMapping("/{gatheringId}/introduction")
    fun upsertIntroduction(
        @PathVariable gatheringId: UUID,
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        @Valid @RequestBody request: UpsertIntroductionRequest,
    ): ResponseEntity<UpsertIntroductionResponse> {
        return ResponseEntity.ok(
            upsertIntroductionUseCase.upsertIntroduction(
                request.toCommand(accountPrincipal.id, gatheringId)
            ).toResponse()
        )
    }
}
