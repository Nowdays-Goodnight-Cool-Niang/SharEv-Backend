package sharev.card_connection.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RequestUpdateCardConnectionDto(
        @NotNull
        @Positive
        Integer targetPinNumber
) {
}
