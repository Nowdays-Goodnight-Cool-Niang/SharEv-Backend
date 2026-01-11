package sharev.member.dto;

import com.querydsl.core.annotations.QueryProjection;
import sharev.member.entity.MemberRoleType;

public record MemberInfoDto(
        Long memberId,
        String name,
        MemberRoleType memberRoleType
) {
    @QueryProjection
    public MemberInfoDto {
    }
}
