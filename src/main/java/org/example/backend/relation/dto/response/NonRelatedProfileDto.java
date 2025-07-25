package org.example.backend.relation.dto.response;

public record NonRelatedProfileDto(
        String name,
        String proudestExperience,
        int iconNumber,
        boolean relationFlag
) implements ResponseRelationProfileDto {

    public NonRelatedProfileDto(String name, int iconNumber, String proudestExperience) {
        this(name, proudestExperience, iconNumber, false);
    }
}
