package org.example.backend.event_account.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.account.entity.Account;
import org.example.backend.event_account.dto.request.RequestPutParticipantInfo;
import org.example.backend.event_account.service.EventAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/participants")
public class EventAccountController {
    private final EventAccountService eventAccountService;

    @PutMapping("/info")
    public ResponseEntity<Void> putParticipants(@AuthenticationPrincipal Account account,
                                                RequestPutParticipantInfo requestPutParticipantInfo) {

        eventAccountService.putParticipant(account, requestPutParticipantInfo);
        return ResponseEntity.noContent().build();
    }
}
