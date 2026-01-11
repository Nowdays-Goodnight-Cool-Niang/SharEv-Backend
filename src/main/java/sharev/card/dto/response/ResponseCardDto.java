package sharev.card.dto.response;

import java.util.List;
import java.util.Map;
import sharev.account.entity.Account;
import sharev.card.entity.Card;
import sharev.gathering.entity.IntroduceTemplate;
import sharev.util.Type;

public record ResponseCardDto(Type type, Long cardId, String name, String email,
                              List<String> linkUrls, int lastIntroduceTemplateVersion,
                              int nowIntroduceTemplateVersion, String introduceTemplateContentText,
                              Map<String, String> introductionText) {

    public ResponseCardDto(Card card, List<String> linkUrls, Account account, int lastIntroduceTemplateVersion,
                           IntroduceTemplate introduceTemplate) {
        this(
                Type.FULL,
                card.getId(),
                account.getName(),
                account.getEmail(),
                linkUrls,
                lastIntroduceTemplateVersion,
                introduceTemplate.getVersion(),
                introduceTemplate.getContent().text(),
                card.getIntroductionText()
        );
    }
}
