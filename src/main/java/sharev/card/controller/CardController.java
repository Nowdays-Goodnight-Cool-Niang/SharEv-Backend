package sharev.card.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sharev.account.entity.Account;
import sharev.card.dto.request.RequestUpdateCardInfoDto;
import sharev.card.dto.response.ResponseCardDto;
import sharev.card.dto.response.ResponseCardInfoDto;
import sharev.card.service.CardService;

@RestController
@RequestMapping("/events/{eventId}/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @Secured("ROLE_USER")
    @PostMapping
    public ResponseEntity<Void> join(@PathVariable("eventId") UUID eventId,
                                     @AuthenticationPrincipal Account account) {
        cardService.join(eventId, account.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @Secured("ROLE_USER")
    @PatchMapping
    public ResponseEntity<ResponseCardInfoDto> updateInfo(@PathVariable("eventId") UUID eventId,
                                                          @AuthenticationPrincipal Account account,
                                                          @Valid @RequestBody RequestUpdateCardInfoDto requestUpdateCardInfoDto) {
        ResponseCardInfoDto responseCardInfoDto =
                cardService.updateInfo(eventId, account.getId(),
                        requestUpdateCardInfoDto.introduce(),
                        requestUpdateCardInfoDto.proudestExperience(),
                        requestUpdateCardInfoDto.toughExperience());

        return ResponseEntity.ok(responseCardInfoDto);
    }

    @Secured("ROLE_USER")
    @GetMapping
    public ResponseEntity<ResponseCardDto> getMyCard(@PathVariable("eventId") UUID eventId,
                                                     @AuthenticationPrincipal Account account) {
        ResponseCardDto myCard = cardService.getMyCard(eventId, account.getId());

        return ResponseEntity.ok(myCard);
    }

    @Secured("ROLE_USER")
    @PreAuthorize("@cardService.hasCompletedCard(authentication.principal, #eventId)")
    @GetMapping("/{pinNumber}")
    public ResponseEntity<ResponseCardDto> getCardByPinNumber(@PathVariable("eventId") UUID eventId,
                                                              @AuthenticationPrincipal Account account,
                                                              @PathVariable(name = "pinNumber") Integer pinNumber) {
        ResponseCardDto participantCard =
                cardService.getCardByPinNumber(eventId, account.getId(), pinNumber);

        return ResponseEntity.ok(participantCard);
    }
}
