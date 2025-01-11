package org.example.backend.event.controller;

import java.util.List;

import org.example.backend.account.entity.Account;
import org.example.backend.event.dto.response.EventDetailsDto;
import org.example.backend.event.dto.response.EventMyParticipantOverviewDto;
import org.example.backend.event.dto.response.EventOverviewDto;
import org.example.backend.event.dto.response.EventParticipantOverviewDto;
import org.example.backend.event.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping()
    public ResponseEntity<List<EventOverviewDto>> fetchList(@AuthenticationPrincipal Account account) {
        return ResponseEntity.ok(eventService.fetchList(account.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDetailsDto> fetchDetails(@AuthenticationPrincipal Account account, @PathVariable Long id) {
        return ResponseEntity.ok(eventService.fetchDetails(account.getId(), id));
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<EventParticipantOverviewDto>> fetchParticipantList(@AuthenticationPrincipal Account account, @PathVariable Long id) {
        return ResponseEntity.ok(eventService.fetchParticipantList(account.getId(), id));
    }

    @GetMapping("/{id}/participant")
    public ResponseEntity<EventMyParticipantOverviewDto> fetchMyParticipant(@AuthenticationPrincipal Account account, @PathVariable Long id) {
        return ResponseEntity.ok(eventService.fetchMyParticipant(account.getId(), id));
    }
}
