package sharev.card.adapter.inbound.web.participant.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import sharev.card.adapter.inbound.web.participant.dto.request.UpdateCardIntroduceRequest
import sharev.card.adapter.inbound.web.participant.dto.response.CardResponse
import sharev.card.adapter.inbound.web.participant.dto.response.JoinCardResponse
import sharev.card.adapter.inbound.web.participant.dto.response.MyShareCodeResponse
import sharev.card.adapter.inbound.web.participant.dto.response.UpdateCardIntroduceResponse
import sharev.card.adapter.inbound.web.participant.mapper.toCommand
import sharev.card.adapter.inbound.web.participant.mapper.toResponse
import sharev.card.application.port.inbound.command.*
import sharev.card.application.port.inbound.usecase.*
import sharev.common.adapter.inbound.security.model.AccountPrincipal
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/gatherings/{gatheringId}/cards")
class CardController(
    private val joinCardUseCase: JoinCardUseCase,
    private val updateCardInfoUseCase: UpdateCardInfoUseCase,
    private val getCardByShareCodeUseCase: GetCardByShareCodeUseCase,
    private val getMyCardUseCase: GetMyCardUseCase,
    private val getMyShareCodeUseCase: GetMyShareCodeUseCase,
    private val getAllCardsUseCase: GetAllCardsUseCase,
) {
    @PostMapping
    fun join(
        @PathVariable gatheringId: UUID,
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
    ): ResponseEntity<JoinCardResponse> {
        val response = joinCardUseCase.join(
            JoinCardCommand(gatheringId, accountPrincipal.id)
        ).toResponse()

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(response)
    }

    @PatchMapping
    fun updateIntroduce(
        @PathVariable gatheringId: UUID,
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        @Valid @RequestBody request: UpdateCardIntroduceRequest,
    ): ResponseEntity<UpdateCardIntroduceResponse> {
        val response = updateCardInfoUseCase.updateIntroduce(
            request.toCommand(gatheringId, accountPrincipal.id)
        ).toResponse()

        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getAllCards(
        @PathVariable gatheringId: UUID,
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        @RequestParam("snapshotTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) snapshotTime: LocalDateTime,
        pageable: Pageable,
    ): ResponseEntity<Page<CardResponse>> {
        return ResponseEntity.ok(
            getAllCardsUseCase.getAllCards(
                GetAllCardsCommand(gatheringId, accountPrincipal.id, snapshotTime, pageable)
            ).map { it.toResponse() }
        )
    }

    @GetMapping("/me")
    fun getMyCard(
        @PathVariable gatheringId: UUID,
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
    ): ResponseEntity<CardResponse> {
        return ResponseEntity.ok(
            getMyCardUseCase.getMyCard(
                GetMyCardCommand(gatheringId, accountPrincipal.id)
            ).toResponse()
        )
    }

    @GetMapping("/me/share-code")
    fun getMyShareCode(
        @PathVariable gatheringId: UUID,
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
    ): ResponseEntity<MyShareCodeResponse> {
        val myShareCode = getMyShareCodeUseCase.getMyShareCode(GetMyShareCodeCommand(gatheringId, accountPrincipal.id))
        return ResponseEntity.ok(MyShareCodeResponse(myShareCode))
    }

    @GetMapping("/by-share-code/{shareCode}")
    fun getCardByShareCode(
        @PathVariable gatheringId: UUID,
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        @PathVariable shareCode: Int,
    ): ResponseEntity<CardResponse> {
        return ResponseEntity.ok(
            getCardByShareCodeUseCase.getCardByShareCode(
                GetCardByShareCodeCommand(gatheringId, accountPrincipal.id, shareCode)
            ).toResponse()
        )
    }
}
