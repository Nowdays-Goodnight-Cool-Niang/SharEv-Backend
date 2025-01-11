package org.example.backend.event.dto.response;

import lombok.Getter;

@Getter
public class EventParticipantOverviewDto {
    private Long id;
    private String name;
    private Integer profileImageId;
    private Boolean connection;

    public EventParticipantOverviewDto(Long id, String name, Integer profileImageId, Long accountId) {
        this.id = id;
        this.name = name;
        this.profileImageId = profileImageId;
        this.connection = accountId != null;
    }
}
