package sharev.card.dto.response;

import java.util.Map;

public record ResponseUpdateCardInfoDto(
        Integer templateVersion,

        Map<String, String> introductionText
) {
}
