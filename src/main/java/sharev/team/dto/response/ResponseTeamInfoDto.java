package sharev.team.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;

public record ResponseTeamInfoDto(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        int headcount
) {

    @QueryProjection
    public ResponseTeamInfoDto {
    }
}
