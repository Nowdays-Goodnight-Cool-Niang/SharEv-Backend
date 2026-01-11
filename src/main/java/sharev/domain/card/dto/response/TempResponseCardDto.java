package sharev.domain.card.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import java.util.Map;
import sharev.domain.account.entity.Account;
import sharev.domain.account.entity.Link;
import sharev.domain.card.entity.Card;
import sharev.domain.gathering.entity.IntroduceTemplate;
import sharev.util.Type;

public record TempResponseCardDto(boolean connectionFlag, Card card, Account account,
                                  IntroduceTemplate introduceTemplate) {

    @QueryProjection
    public TempResponseCardDto {
    }

    public ResponseCardDto toResponseDto(List<Link> links, int lastIntroduceTemplateVersion) {
        return new ResponseCardDto(
                getConnectionType(this.connectionFlag),
                this.card.getId(),
                this.account.getName(),
                showIfConnected(this.connectionFlag, this.account.getEmail(), ""),
                showIfConnected(this.connectionFlag, links.stream().map(Link::getLinkUrl).toList(), List.of()),
                lastIntroduceTemplateVersion,
                this.introduceTemplate.getVersion(),
                this.introduceTemplate.getContent().text(),
                showIfConnected(this.connectionFlag, this.card.getIntroductionText(), Map.of())
        );
    }

    private static Type getConnectionType(boolean connectionFlag) {
        return connectionFlag ? Type.FULL : Type.MINIMUM;
    }

    private static <T> T showIfConnected(boolean connectionFlag, T value, T fallback) {
        return connectionFlag ? value : fallback;
    }
}
