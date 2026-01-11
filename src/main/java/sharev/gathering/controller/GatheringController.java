package sharev.gathering.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sharev.account.entity.Account;
import sharev.card.dto.response.ResponseParticipantFlagDto;
import sharev.card.service.CardService;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class GatheringController {

    private final CardService cardService;

    @Secured("ROLE_USER")
    @GetMapping("/{eventId}")
    public ResponseEntity<ResponseParticipantFlagDto> isJoined(@PathVariable UUID eventId,
                                                               @AuthenticationPrincipal Account account
    ) {
        return ResponseEntity.ok(cardService.isJoined(eventId, account.getId()));
    }
}
