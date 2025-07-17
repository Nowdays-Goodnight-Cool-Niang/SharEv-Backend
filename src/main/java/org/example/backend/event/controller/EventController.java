package org.example.backend.event.controller;

import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.backend.account.entity.Account;
import org.example.backend.participant.dto.request.RequestUpdateParticipantInfoDto;
import org.example.backend.participant.dto.response.ParticipantProfileDto;
import org.example.backend.participant.dto.response.ResponseParticipantInfoDto;
import org.example.backend.participant.service.ParticipantService;
import org.example.backend.social_dex.dto.request.RequestUpdateSocialDexDto;
import org.example.backend.social_dex.dto.response.ResponseSocialDexDto;
import org.example.backend.social_dex.dto.response.ResponseSocialDexParticipantProfileDto;
import org.example.backend.social_dex.service.SocialDexService;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    private final ParticipantService participantService;
    private final SocialDexService socialDexService;

    @PostMapping("/{eventId}/participants")
    public ResponseEntity<Void> join(@PathVariable("eventId") UUID eventId,
                                     @AuthenticationPrincipal Account account) {
        participantService.join(eventId, account.getId());

        return ResponseEntity.status(HTTPResponse.SC_CREATED)
                .build();
    }

    @PatchMapping("/{eventId}/participants")
    public ResponseEntity<ResponseParticipantInfoDto> updateInfo(@PathVariable("eventId") UUID eventId,
                                                                 @AuthenticationPrincipal Account account,
                                                                 @RequestBody RequestUpdateParticipantInfoDto requestUpdateParticipantInfoDto) {
        participantService.updateInfo(requestUpdateParticipantInfoDto.eventId(), account.getId(),
                requestUpdateParticipantInfoDto.introduce(), requestUpdateParticipantInfoDto.reminderExperience(),
                requestUpdateParticipantInfoDto.wantAgainExperience());

        ResponseParticipantInfoDto responseParticipantInfoDto = participantService.getParticipantInfo(eventId,
                account.getId());
        return ResponseEntity.ok(responseParticipantInfoDto);
    }

    @GetMapping("/{eventId}/participants/{pinNumber}")
    public ResponseEntity<ParticipantProfileDto> getParticipantInfo(@PathVariable("eventId") UUID eventId,
                                                                    @AuthenticationPrincipal Account account,
                                                                    @PathVariable(name = "pinNumber") Integer pinNumber) {
        ParticipantProfileDto participantProfile =
                participantService.getParticipantProfile(eventId, account.getId(), pinNumber);

        return ResponseEntity.ok(participantProfile);
    }

    @Secured("ROLE_USER")
    @GetMapping("/{eventId}/participants")
    public ResponseEntity<ResponseParticipantInfoDto> getMyParticipantInfo(@PathVariable("eventId") UUID eventId,
                                                                           @AuthenticationPrincipal Account account) {
        ResponseParticipantInfoDto responseParticipantInfoDto =
                participantService.getParticipantInfo(eventId, account.getId());

        return ResponseEntity.ok(responseParticipantInfoDto);
    }

    @PostMapping("/{eventId}/participants/social-dex")
    public ResponseEntity<ResponseSocialDexDto> register(@PathVariable("eventId") UUID eventId,
                                                         @AuthenticationPrincipal Account account,
                                                         @RequestBody RequestUpdateSocialDexDto requestUpdateSocialDexDto) {
        ResponseSocialDexDto responseSocialDexDto =
                socialDexService.register(eventId, account.getId(), requestUpdateSocialDexDto.targetPinNumber());

        return ResponseEntity.ok(responseSocialDexDto);
    }

    @Secured("ROLE_USER")
    @GetMapping("/{eventId}/participants/social-dex")
    public ResponseEntity<ResponseSocialDexParticipantProfileDto> getSocialDex(@PathVariable("eventId") UUID eventId,
                                                                               @AuthenticationPrincipal Account account,
                                                                               @RequestParam("snapshotTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime snapshotTime,
                                                                               Pageable pageable) {
        return ResponseEntity.ok(socialDexService.getSocialDex(eventId, account.getId(), snapshotTime, pageable));
    }
}
