package sharev.card_connection.controller;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import sharev.account.entity.Account;
import sharev.card_connection.dto.request.RequestUpdateCardConnectionDto;
import sharev.card_connection.dto.response.ResponseConnectionInfoDto;
import sharev.card_connection.service.CardConnectionService;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events/{eventId}/card-connections")
@RequiredArgsConstructor
public class CardConnectionController {

    private final CardConnectionService cardConnectionService;

    @Secured("ROLE_USER")
    @PreAuthorize("@cardService.hasCompletedCard(authentication.principal, #eventId)")
    @PostMapping
    public ResponseEntity<Void> connect(@PathVariable("eventId") UUID eventId,
                                        @AuthenticationPrincipal Account account,
                                        @Valid @RequestBody RequestUpdateCardConnectionDto requestUpdateCardConnectionDto) {

        cardConnectionService.connect(eventId, account.getId(), requestUpdateCardConnectionDto.targetPinNumber());

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @Secured("ROLE_USER")
    @GetMapping
    public ResponseEntity<ResponseConnectionInfoDto> getConnectionInfos(@PathVariable("eventId") UUID eventId,
                                                                        @AuthenticationPrincipal Account account,
                                                                        @RequestParam("snapshotTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime snapshotTime,
                                                                        Pageable pageable) {
        return ResponseEntity.ok(
                cardConnectionService.getConnectionInfos(eventId, account.getId(), snapshotTime, pageable));
    }
}
