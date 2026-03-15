package sharev.domain.gathering.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import sharev.domain.gathering.entity.GatheringVisibleType;

public record RequestUpdateGatheringDto(
        @NotNull GatheringVisibleType visible,
        @NotBlank String title,
        @NotBlank String content,
        @NotNull LocalDateTime startAt,
        @NotNull LocalDateTime endAt,
        @NotBlank String place,
        String imageUrl,
        String gatheringUrl,
        String contact,
        @NotNull LocalDateTime registerStartAt,
        @NotNull LocalDateTime registerEndAt
) {
}
