package org.example.backend.event.controller;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.backend.account.entity.Account;
import org.example.backend.social_dex.dto.request.RequestUpdateSocialDexDto;
import org.example.backend.social_dex.dto.response.ResponseSocialDexDto;
import org.example.backend.social_dex.dto.response.ResponseSocialDexInfoDto;
import org.example.backend.social_dex.service.SocialDexService;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final SocialDexService socialDexService;

    @PostMapping("/{eventId}/participants")
    public ResponseEntity<ResponseSocialDexDto> updateSocialDex(@PathVariable("eventId") UUID eventId,
                                                                @AuthenticationPrincipal Account account,
                                                                @RequestBody RequestUpdateSocialDexDto requestUpdateSocialDexDto) {
        ResponseSocialDexDto responseSocialDexDto =
                socialDexService.updateSocialDex(eventId, account.getId(), requestUpdateSocialDexDto.targetPinNumber());

        return ResponseEntity.ok(responseSocialDexDto);
    }

    @Secured("ROLE_USER")
    @GetMapping("/{eventId}/participants")
    public ResponseEntity<ResponseSocialDexInfoDto> getSocialDex(@PathVariable("eventId") UUID eventId,
                                                                 @AuthenticationPrincipal Account account,
                                                                 @RequestParam("snapshotTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime snapshotTime,
                                                                 Pageable pageable) {
        return ResponseEntity.ok(socialDexService.getSocialDex(eventId, account.getId(), snapshotTime, pageable));
    }
}
