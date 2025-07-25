package org.example.backend.relation.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import org.example.backend.util.Type;

public record RelationProfileDto(
        Type type,
        Long profileId,
        String name,
        String email,
        String linkedinUrl,
        String githubUrl,
        String instagramUrl,
        int iconNumber,
        String introduce,
        String proudestExperience,
        String toughExperience,
        boolean relationFlag
) implements ResponseRelationProfileDto {

    @QueryProjection
    public RelationProfileDto(Long profileId,
                              String name,
                              String email,
                              String linkedinUrl,
                              String githubUrl,
                              String instagramUrl,
                              int iconNumber,
                              String introduce,
                              String proudestExperience,
                              String toughExperience,
                              boolean relationFlag) {
        this(relationFlag ? Type.FULL : Type.MINIMUM, profileId, name, email, linkedinUrl, githubUrl, instagramUrl,
                iconNumber, introduce, proudestExperience, toughExperience, relationFlag);
    }

    public ResponseRelationProfileDto convertIfNonRelation() {
        if (relationFlag) {
            return this;
        }

        return new NonRelatedProfileDto(name, iconNumber, proudestExperience);
    }
}
