package sharev.domain.gathering.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;
import sharev.domain.gathering.entity.Gathering;
import sharev.domain.gathering.entity.GatheringVisibleType;

public record ResponseGatheringDetailDto(
        UUID id,
        GatheringVisibleType visible,
        String title,
        String content,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String place,
        String imageUrl,
        String gatheringUrl,
        String contact,
        LocalDateTime registerStartAt,
        LocalDateTime registerEndAt
) {
    public ResponseGatheringDetailDto(Gathering gathering) {
        this(gathering.getId(), gathering.getVisible(), gathering.getTitle(), gathering.getContent(),
                gathering.getStartAt(), gathering.getEndAt(), gathering.getPlace(),
                gathering.getImageUrl(), gathering.getGatheringUrl(), gathering.getContact(),
                gathering.getRegisterStartAt(), gathering.getRegisterEndAt());
    }
}
