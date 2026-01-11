package sharev.card.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record RequestUpdateCardInfoDto(
        @NotNull
        Integer version,

        @NotNull
        Map<String, String> introductionText
) {
}
