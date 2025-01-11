package org.example.backend.participant.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.account.entity.Account;
import org.example.backend.participant.dto.request.RequestPutParticipantInfo;
import org.example.backend.participant.service.ParticipantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/participants")
public class ParticipantController {
    private final ParticipantService participantService;

    @PutMapping("/info")
    public ResponseEntity<Void> putParticipants(@AuthenticationPrincipal Account account,
                                                RequestPutParticipantInfo requestPutParticipantInfo) {

        participantService.putParticipant(account, requestPutParticipantInfo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/info/{id}")
    public ResponseEntity<?> fetchParticipants(@AuthenticationPrincipal Account account, @PathVariable Long id) {
        return ResponseEntity.ok(participantService.fetchParticipants(account.getId(), id));
    }

}
