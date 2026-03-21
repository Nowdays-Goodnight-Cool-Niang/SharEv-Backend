package sharev.domain.member.dto.request;

import jakarta.validation.constraints.NotNull;
import sharev.domain.member.entity.MemberRoleType;

public record RequestUpdateMemberRoleDto(
        @NotNull MemberRoleType role
) {
}
