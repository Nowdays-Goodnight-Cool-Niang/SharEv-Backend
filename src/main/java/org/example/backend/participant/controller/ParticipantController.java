package org.example.backend.participant.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.account.entity.Account;
import org.example.backend.participant.dto.request.RequestPutParticipantInfo;
import org.example.backend.participant.dto.response.ResponseGetParticipantInfo;
import org.example.backend.participant.service.ParticipantService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/participants")
public class ParticipantController {
    private final ParticipantService participantService;

    @Value("${origin.url}")
    private String originUrl;

    @PutMapping("/info")
    public ResponseEntity<Void> putParticipants(@AuthenticationPrincipal Account account,
                                                @RequestBody RequestPutParticipantInfo requestPutParticipantInfo) {

        participantService.putParticipant(account, requestPutParticipantInfo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<ResponseGetParticipantInfo> fetchParticipants(@AuthenticationPrincipal Account account, @PathVariable Long id) {
        return ResponseEntity.ok(participantService.fetchParticipants(account.getId(), id));
    }

    @GetMapping("/register/mine/{eventId}")
    public ResponseEntity<String> getRegisterLink(@AuthenticationPrincipal Account account, @PathVariable Long eventId) {
        Long participantId = participantService.getParticipantId(account, eventId);
        return ResponseEntity.ok(originUrl + "register/" + participantId);
    }

    @GetMapping("/register/{participantId}")
    public ResponseEntity<String> getQrLink(@AuthenticationPrincipal Account account, @PathVariable Long participantId) {

        if (account != null) {
            participantService.putFoundParticipant(account.getId(), participantId);
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("/participants/info/" + participantId));

        return new ResponseEntity<>(httpHeaders, HttpStatus.MULTIPLE_CHOICES);
    }
}
