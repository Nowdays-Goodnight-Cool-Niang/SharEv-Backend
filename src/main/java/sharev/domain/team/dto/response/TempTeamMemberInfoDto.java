package sharev.domain.team.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record TempTeamMemberInfoDto(
        String name,
        String email
) {

    @QueryProjection
    public TempTeamMemberInfoDto {
    }
}
