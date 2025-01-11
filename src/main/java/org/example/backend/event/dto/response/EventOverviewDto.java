package org.example.backend.event.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class EventOverviewDto {
    private Long id;
    private String title;
    private String imageUrl;
    private String content;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    public EventOverviewDto(
            Long id,
            String title,
            String imageUrl,
            String content,
            LocalDateTime startedAt,
            LocalDateTime endedAt) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.content = content;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
    }
}
