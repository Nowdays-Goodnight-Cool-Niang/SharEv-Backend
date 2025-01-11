package org.example.backend.event.service;

import java.util.List;

import org.example.backend.event.dto.response.EventDetailsDto;
import org.example.backend.event.dto.response.EventOverviewDto;
import org.example.backend.event.dto.response.EventParticipantOverviewDto;
import org.example.backend.event.repository.EventRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public List<EventOverviewDto> fetchList(Long accountId) {
        return eventRepository.fetchList();
    }

    public EventDetailsDto fetchDetails(Long accountId, Long eventId) {
        return eventRepository.fetchBy(eventId, accountId);
    }

    public List<EventParticipantOverviewDto> fetchParticipantList(Long accountId, Long eventId) {
        return eventRepository.fetchParticipantList(eventId, accountId);
    }
}
