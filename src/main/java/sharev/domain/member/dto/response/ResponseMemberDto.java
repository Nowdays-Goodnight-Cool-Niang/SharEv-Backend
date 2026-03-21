package sharev.domain.member.dto.response;

import sharev.domain.member.entity.Member;
import sharev.domain.member.entity.MemberRoleType;
import sharev.domain.member.entity.MemberStatusType;

public record ResponseMemberDto(
        Long memberId,
        String name,
        String email,
        MemberRoleType role,
        MemberStatusType status
) {
    public ResponseMemberDto(Member member) {
        this(
                member.getId(),
                member.getAccount().getName(),
                member.getAccount().getEmail(),
                member.getRole(),
                member.getStatus()
        );
    }
}
