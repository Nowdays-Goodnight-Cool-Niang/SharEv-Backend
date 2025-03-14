package org.example.backend.shareCard.dto.request;

public record RequestUpdateShareCardDto(
        String teamName,

        String position,

        String introductionText
) {
}
