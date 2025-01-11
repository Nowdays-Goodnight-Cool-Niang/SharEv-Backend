package org.example.backend.event.repository;

import java.util.List;

import org.example.backend.event.dto.response.EventDetailsDto;
import org.example.backend.event.dto.response.EventOverviewDto;

public interface EventRepositoryExtension {

    List<EventOverviewDto> fetchList();

    EventDetailsDto fetchBy(Long eventId, Long accountId);

}
