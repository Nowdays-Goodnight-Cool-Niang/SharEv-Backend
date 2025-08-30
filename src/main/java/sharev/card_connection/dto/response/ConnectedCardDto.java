package sharev.card_connection.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import sharev.util.Type;

public record ConnectedCardDto(
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
) implements ResponseConnectionCardDto {

    @QueryProjection
    public ConnectedCardDto(Long profileId,
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

    public ResponseConnectionCardDto convertIfNonRelation() {
        if (relationFlag) {
            return this;
        }

        return new NonConnectedCardDto(name, iconNumber, proudestExperience);
    }
}
