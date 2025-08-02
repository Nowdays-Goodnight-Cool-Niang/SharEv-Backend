package org.example.backend.relation.dto.response;

import org.example.backend.util.Type;

public record NonRelatedProfileDto(
        Type type,
        String name,
        String proudestExperience,
        int iconNumber,
        boolean relationFlag
) implements ResponseRelationProfileDto {

    public NonRelatedProfileDto(String name, int iconNumber, String proudestExperience) {
        this(Type.MINIMUM, name, proudestExperience, iconNumber, false);
    }
}
