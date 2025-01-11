package org.example.backend.participant.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.account.entity.Account;
import org.example.backend.participant.dto.request.RequestPutParticipantInfo;
import org.example.backend.participant.dto.response.ResponseGetParticipantInfo;
import org.example.backend.participant.service.ParticipantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/participants")
public class ParticipantController {
    private final ParticipantService participantService;

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

    @PutMapping("/{id}")
    public ResponseEntity<Void> putFoundParticipants(@AuthenticationPrincipal Account account, @PathVariable Long id) {
        participantService.putFoundParticipant(account.getId(), id);
        return ResponseEntity.noContent().build();
    }

}
