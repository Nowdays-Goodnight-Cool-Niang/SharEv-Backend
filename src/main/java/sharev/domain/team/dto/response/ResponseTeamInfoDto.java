package sharev.domain.team.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;
import sharev.domain.member.entity.MemberRoleType;

public record ResponseTeamInfoDto(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        MemberRoleType memberRoleType,
        int headcount
) {

    @QueryProjection
    public ResponseTeamInfoDto {
    }
}
