package sharev.domain.connection.event;

import java.util.UUID;

public record ShowCardEvent(

        UUID eventId,

        Long accountId,

        Long targetCardId
) {
}
