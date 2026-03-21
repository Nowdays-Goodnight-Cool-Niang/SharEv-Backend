package sharev.domain.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RequestInviteMemberDto(
        @NotBlank @Email String email
) {
}
