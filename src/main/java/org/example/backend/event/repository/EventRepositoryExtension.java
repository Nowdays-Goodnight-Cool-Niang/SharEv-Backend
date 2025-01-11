package org.example.backend.event.repository;

import java.util.List;

import org.example.backend.event.dto.response.EventDetailsDto;
import org.example.backend.event.dto.response.EventMyParticipantOverviewDto;
import org.example.backend.event.dto.response.EventOverviewDto;
import org.example.backend.event.dto.response.EventParticipantOverviewDto;

public interface EventRepositoryExtension {

    List<EventOverviewDto> fetchList();

    EventDetailsDto fetchBy(Long eventId, Long accountId);

    List<EventParticipantOverviewDto> fetchParticipantList(Long eventId, Long accountId);

    EventMyParticipantOverviewDto fetchMyParticipant(Long eventId, Long accountId);

}
