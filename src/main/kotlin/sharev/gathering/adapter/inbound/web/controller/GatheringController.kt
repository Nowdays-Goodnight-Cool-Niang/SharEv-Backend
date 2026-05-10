package sharev.gathering.adapter.inbound.web.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import sharev.common.adapter.inbound.security.model.AccountPrincipal
import sharev.gathering.adapter.inbound.web.dto.request.CreateGatheringRequest
import sharev.gathering.adapter.inbound.web.dto.request.UpdateGatheringRequest
import sharev.gathering.adapter.inbound.web.dto.response.*
import sharev.gathering.adapter.inbound.web.mapper.toCommand
import sharev.gathering.adapter.inbound.web.mapper.toResponse
import sharev.gathering.application.port.inbound.usecase.*
import java.util.*

@RestController
class GatheringController(
    private val createGatheringUseCase: CreateGatheringUseCase,
    private val getGatheringUseCase: GetGatheringUseCase,
    private val updateGatheringUseCase: UpdateGatheringUseCase,
    private val deleteGatheringUseCase: DeleteGatheringUseCase,
    private val getIntroduceTemplateUseCase: GetIntroduceTemplateUseCase,
    private val checkGatheringParticipantUseCase: CheckGatheringParticipantUseCase,
) {

    @GetMapping("/gatherings/{gatheringId}")
    fun isParticipant(
        @PathVariable gatheringId: UUID,
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
    ): ResponseEntity<ParticipantResponse> {
        return ResponseEntity.ok(
            checkGatheringParticipantUseCase.isParticipant(accountPrincipal.id, gatheringId)
                .toResponse()
        )
    }

    @PostMapping("/teams/{teamId}/gatherings")
    fun createGathering(
        @PathVariable teamId: Long,
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        @Valid @RequestBody request: CreateGatheringRequest,
    ): ResponseEntity<CreateGatheringResponse> {
        val response = createGatheringUseCase.create(
            request.toCommand(accountPrincipal.id, teamId)
        ).toResponse()

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(response)
    }

    // TODO: 템플릿 업데이트
    // TODO: content와 placeholder key가 다르다면 에러
    // TODO: 이전 content key와 업데이트 key 일치(혹은 부분일치) 시 단순 템플릿 변경이므로 버전 그대로, 다르다면(추가된 게 있다면) 버전 업

    @GetMapping("/teams/{teamId}/gatherings")
    fun getGatherings(
        @PathVariable teamId: Long,
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
    ): ResponseEntity<List<GatheringDetailResponse>> {
        return ResponseEntity.ok(
            getGatheringUseCase.getGatherings(accountPrincipal.id, teamId)
                .map { it.toResponse() }
        )
    }

    @GetMapping("/teams/{teamId}/gatherings/{gatheringId}")
    fun getGathering(
        @PathVariable teamId: Long,
        @PathVariable gatheringId: UUID,
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
    ): ResponseEntity<GatheringDetailResponse> {
        return ResponseEntity.ok(
            getGatheringUseCase.getGathering(
                accountPrincipal.id, teamId, gatheringId
            ).toResponse()
        )
    }

    @PatchMapping("/teams/{teamId}/gatherings/{gatheringId}")
    fun updateGathering(
        @PathVariable teamId: Long,
        @PathVariable gatheringId: UUID,
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        @Valid @RequestBody request: UpdateGatheringRequest,
    ): ResponseEntity<GatheringDetailResponse> {
        return ResponseEntity.ok(
            updateGatheringUseCase.update(
                request.toCommand(
                    accountPrincipal.id,
                    teamId,
                    gatheringId
                )
            ).toResponse()
        )
    }

    @DeleteMapping("/teams/{teamId}/gatherings/{gatheringId}")
    fun deleteGathering(
        @PathVariable teamId: Long,
        @PathVariable gatheringId: UUID,
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
    ): ResponseEntity<DeleteGatheringResponse> {
        val response = deleteGatheringUseCase.delete(
            accountPrincipal.id, teamId, gatheringId
        ).toResponse()
        return ResponseEntity.ok(response)
    }

    @GetMapping("/gatherings/{gatheringId}/template")
    fun getTemplate(
        @PathVariable gatheringId: UUID,
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
    ): ResponseEntity<IntroduceTemplateResponse> {
        return ResponseEntity.ok(
            getIntroduceTemplateUseCase.getLatestTemplate(
                gatheringId, accountPrincipal.id
            ).toResponse()
        )
    }
}
