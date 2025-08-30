package sharev.card.dto.response;

import sharev.card.entity.Card;
import sharev.util.Type;

public record ResponseCardDto(Type type, Long profileId, String name, String email, String linkedinUrl,
                              String githubUrl,
                              String instagramUrl,
                              int iconNumber,
                              int pinNumber,
                              String introduce,
                              String proudestExperience,
                              String toughExperience,
                              boolean registerRequireFlag) {

    public ResponseCardDto(Long profileId, String name, String email, String linkedinUrl,
                           String githubUrl,
                           String instagramUrl, int iconNumber, int pinNumber, String introduce,
                           String proudestExperience, String toughExperience, boolean registerRequireFlag) {

        this(Type.FULL, profileId, name, email, linkedinUrl, githubUrl, instagramUrl, iconNumber, pinNumber, introduce,
                proudestExperience, toughExperience, registerRequireFlag);
    }

    public ResponseCardDto(Card card, boolean registerRequireFlag) {
        this(Type.FULL, card.getId(), card.getAccount().getName(), card.getAccount().getEmail(),
                card.getAccount().getLinkedinUrl(), card.getAccount().getGithubUrl(),
                card.getAccount().getInstagramUrl(), card.getIconNumber(), card.getPinNumber(),
                card.getIntroduce(), card.getProudestExperience(), card.getToughExperience(),
                registerRequireFlag);
    }
}
