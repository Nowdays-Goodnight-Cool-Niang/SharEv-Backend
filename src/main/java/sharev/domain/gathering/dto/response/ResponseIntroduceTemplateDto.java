package sharev.domain.gathering.dto.response;

import java.util.Map;
import sharev.domain.gathering.entity.IntroduceTemplate;

public record ResponseIntroduceTemplateDto(
        int version,
        String text,
        Map<String, String> fieldPlaceholders
) {
    public ResponseIntroduceTemplateDto(IntroduceTemplate introduceTemplate) {
        this(
                introduceTemplate.getVersion(),
                introduceTemplate.getContent().text(),
                introduceTemplate.getContent().fieldPlaceholders()
        );
    }
}
