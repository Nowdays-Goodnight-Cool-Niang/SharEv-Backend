package org.example.backend.event.controller;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.backend.account.entity.Account;
import org.example.backend.profile.dto.request.RequestUpdateProfileInfoDto;
import org.example.backend.profile.dto.response.ResponseParticipantFlagDto;
import org.example.backend.profile.dto.response.ResponseProfileDto;
import org.example.backend.profile.dto.response.ResponseProfileInfoDto;
import org.example.backend.profile.service.ProfileService;
import org.example.backend.relation.dto.request.RequestUpdateRelationDto;
import org.example.backend.relation.dto.response.ResponseRelationInfoDto;
import org.example.backend.relation.service.RelationService;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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

    private final ProfileService profileService;
    private final RelationService relationService;

    @PostMapping("/{eventId}/profiles")
    public ResponseEntity<Void> join(@PathVariable("eventId") UUID eventId,
                                     @AuthenticationPrincipal Account account) {
        profileService.join(eventId, account.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @PatchMapping("/{eventId}/profiles")
    public ResponseEntity<ResponseProfileInfoDto> updateInfo(@PathVariable("eventId") UUID eventId,
                                                             @AuthenticationPrincipal Account account,
                                                             @RequestBody RequestUpdateProfileInfoDto requestUpdateProfileInfoDto) {
        ResponseProfileInfoDto responseProfileInfoDto =
                profileService.updateInfo(eventId, account.getId(),
                        requestUpdateProfileInfoDto.introduce(),
                        requestUpdateProfileInfoDto.proudestExperience(),
                        requestUpdateProfileInfoDto.toughExperience());

        return ResponseEntity.ok(responseProfileInfoDto);
    }

    @GetMapping("/{eventId}/profiles/{pinNumber}")
    public ResponseEntity<ResponseProfileDto> getProfileByPinNumber(@PathVariable("eventId") UUID eventId,
                                                                    @AuthenticationPrincipal Account account,
                                                                    @PathVariable(name = "pinNumber") Integer pinNumber) {
        ResponseProfileDto participantProfile =
                profileService.getProfileByPinNumber(eventId, account.getId(), pinNumber);

        return ResponseEntity.ok(participantProfile);
    }

    @Secured("ROLE_USER")
    @GetMapping("/{eventId}/profiles")
    public ResponseEntity<ResponseProfileDto> getMyProfile(@PathVariable("eventId") UUID eventId,
                                                           @AuthenticationPrincipal Account account) {
        ResponseProfileDto myProfile = profileService.getMyProfile(eventId, account.getId());

        return ResponseEntity.ok(myProfile);
    }

    @Secured("ROLE_USER")
    @PostMapping("/{eventId}/participants")
    public ResponseEntity<Void> register(@PathVariable("eventId") UUID eventId,
                                         @AuthenticationPrincipal Account account,
                                         @Valid @RequestBody RequestUpdateRelationDto requestUpdateRelationDto) {

        relationService.register(eventId, account.getId(), requestUpdateRelationDto.targetPinNumber());

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @Secured("ROLE_USER")
    @GetMapping("/{eventId}/participants")
    public ResponseEntity<ResponseRelationInfoDto> getParticipants(@PathVariable("eventId") UUID eventId,
                                                                   @AuthenticationPrincipal Account account,
                                                                   @RequestParam("snapshotTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime snapshotTime,
                                                                   Pageable pageable) {
        return ResponseEntity.ok(relationService.getParticipants(eventId, account.getId(), snapshotTime, pageable));
    }

    @Secured("ROLE_USER")
    @GetMapping("/{eventId}")
    public ResponseEntity<ResponseParticipantFlagDto> isParticipant(@PathVariable("eventId") UUID eventId,
                                                                    @AuthenticationPrincipal Account account
    ) {
        return ResponseEntity.ok(profileService.isParticipant(eventId, account.getId()));
    }
}
