package sharev.domain.gathering.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sharev.domain.account.entity.Account;
import sharev.domain.card.dto.response.ResponseParticipantFlagDto;
import sharev.domain.card.service.CardService;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class GatheringController {

    private final CardService cardService;

    @GetMapping("/{eventId}")
    public ResponseEntity<ResponseParticipantFlagDto> isJoined(@PathVariable UUID eventId,
                                                               @AuthenticationPrincipal Account account
    ) {
        return ResponseEntity.ok(cardService.isJoined(eventId, account.getId()));
    }
}
