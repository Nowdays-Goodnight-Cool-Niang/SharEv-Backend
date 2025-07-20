package org.example.backend.relation.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record RelationProfileDto(
        Long profileId,
        String name,
        String email,
        String linkedinUrl,
        String githubUrl,
        String instagramUrl,
        String introduce,
        String proudestExperience,
        String toughExperience,
        boolean relationFlag
) implements ResponseRelationProfileDto {

    @QueryProjection
    public RelationProfileDto {
        // QueryDSL 위한 빈 생성자
    }

    public ResponseRelationProfileDto convertIfNonRelation() {
        if (relationFlag) {
            return this;
        }

        return new NonRelatedProfileDto(name, proudestExperience);
    }
}
