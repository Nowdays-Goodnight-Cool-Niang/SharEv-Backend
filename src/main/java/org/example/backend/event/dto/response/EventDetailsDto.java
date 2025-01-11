package org.example.backend.event.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class EventDetailsDto {
    private Long id;
    private String title;
    private String imageUrl;
    private String content;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String organizer;
    private String place;
    private String eventUrl;
    private Boolean registration;
    private Long totalRegistration;

    public EventDetailsDto(
            Long id,
            String title,
            String imageUrl,
            String content,
            String organizer,
            String place,
            String eventUrl,
            LocalDateTime startedAt,
            LocalDateTime endedAt,
            Long accountId) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.content = content;
        this.organizer = organizer;
        this.place = place;
        this.eventUrl = eventUrl;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.registration = accountId != null;
        this.totalRegistration = null;
    }

    public void setTotalRegistration(Long count) {
        this.totalRegistration = count;
    }
}
