package sharev.card.adapter.inbound.web.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import sharev.card.adapter.inbound.web.dto.request.UpdateCardIntroduceRequest
import sharev.card.adapter.inbound.web.dto.response.CardResponse
import sharev.card.adapter.inbound.web.dto.response.JoinCardResponse
import sharev.card.adapter.inbound.web.dto.response.MyPinNumberResponse
import sharev.card.adapter.inbound.web.dto.response.UpdateCardIntroduceResponse
import sharev.card.adapter.inbound.web.mapper.toCommand
import sharev.card.adapter.inbound.web.mapper.toMyPinNumberResponse
import sharev.card.adapter.inbound.web.mapper.toResponse
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
    private val getCardByPinNumberUseCase: GetCardByPinNumberUseCase,
    private val getMyCardUseCase: GetMyCardUseCase,
    private val getMyPinNumberUseCase: GetMyPinNumberUseCase,
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

    @GetMapping("/me/pin")
    fun getMyPinNumber(
        @PathVariable gatheringId: UUID,
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
    ): ResponseEntity<MyPinNumberResponse> {
        return ResponseEntity.ok(
            getMyPinNumberUseCase.getMyPinNumber(
                GetMyPinNumberCommand(gatheringId, accountPrincipal.id)
            ).toMyPinNumberResponse()
        )
    }

    @GetMapping("by-pin/{pinNumber}")
    fun getCardByPinNumber(
        @PathVariable gatheringId: UUID,
        @AuthenticationPrincipal accountPrincipal: AccountPrincipal,
        @PathVariable pinNumber: Int,
    ): ResponseEntity<CardResponse> {
        return ResponseEntity.ok(
            getCardByPinNumberUseCase.getCardByPinNumber(
                GetCardByPinNumberCommand(gatheringId, accountPrincipal.id, pinNumber)
            ).toResponse()
        )
    }
}
