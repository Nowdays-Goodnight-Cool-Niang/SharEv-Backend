package sharev.domain.card.controller;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sharev.domain.account.entity.Account;
import sharev.domain.card.dto.request.RequestUpdateCardInfoDto;
import sharev.domain.card.dto.response.ResponseCardDto;
import sharev.domain.card.dto.response.ResponseUpdateCardInfoDto;
import sharev.domain.card.service.CardService;

@RestController
@RequestMapping("/gathering/{gatheringId}/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<Void> join(@PathVariable UUID gatheringId,
                                     @AuthenticationPrincipal Account account) {
        cardService.join(gatheringId, account.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @PatchMapping
    public ResponseEntity<ResponseUpdateCardInfoDto> updateInfo(@PathVariable UUID gatheringId,
                                                                @AuthenticationPrincipal Account account,
                                                                @Valid @RequestBody RequestUpdateCardInfoDto requestUpdateCardInfoDto) {
        ResponseUpdateCardInfoDto responseUpdateCardInfoDto = cardService.updateInfo(
                gatheringId, account.getId(),
                requestUpdateCardInfoDto.version(),
                requestUpdateCardInfoDto.introductionText());

        return ResponseEntity.ok(responseUpdateCardInfoDto);
    }

    @PreAuthorize("@cardService.hasCompletedCard(authentication.principal, #gatheringId)")
    @GetMapping
    public ResponseEntity<Page<ResponseCardDto>> getAllCard(@PathVariable UUID gatheringId,
                                                            @AuthenticationPrincipal Account account,
                                                            @RequestParam("snapshotTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime snapshotTime,
                                                            Pageable pageable) {

        return ResponseEntity.ok(cardService.getAllCard(gatheringId, account.getId(), snapshotTime, pageable));
    }

    @PreAuthorize("@cardService.hasCompletedCard(authentication.principal, #gatheringId)")
    @GetMapping("/me")
    public ResponseEntity<ResponseCardDto> getMyCard(@PathVariable UUID gatheringId,
                                                     @AuthenticationPrincipal Account account) {
        ResponseCardDto myCard = cardService.getMyCard(gatheringId, account.getId());

        return ResponseEntity.ok(myCard);
    }

    @PreAuthorize("@cardService.hasCompletedCard(authentication.principal, #gatheringId)")
    @GetMapping("by-pin/{pinNumber}")
    public ResponseEntity<ResponseCardDto> getCardByPinNumber(@PathVariable UUID gatheringId,
                                                              @AuthenticationPrincipal Account account,
                                                              @PathVariable Integer pinNumber) {
        ResponseCardDto participantCard =
                cardService.getCardByPinNumber(gatheringId, account.getId(), pinNumber);

        return ResponseEntity.ok(participantCard);
    }
}
