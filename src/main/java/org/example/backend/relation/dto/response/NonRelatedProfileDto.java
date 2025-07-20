package org.example.backend.relation.dto.response;

public record NonRelatedProfileDto(
        String name,
        String proudestExperience,
        boolean relationFlag
) implements ResponseRelationProfileDto {

    public NonRelatedProfileDto(String name, String proudestExperience) {
        this(name, proudestExperience, false);
    }
}
