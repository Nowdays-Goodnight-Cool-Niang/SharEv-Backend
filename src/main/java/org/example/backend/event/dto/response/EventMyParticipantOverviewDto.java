package org.example.backend.event.dto.response;

import lombok.Getter;

@Getter
public class EventMyParticipantOverviewDto {
    private Long id;
    private Integer profileImageId;

    public EventMyParticipantOverviewDto(Long id, Integer profileImageId) {
        this.id = id;
        this.profileImageId = profileImageId;
    }
}
