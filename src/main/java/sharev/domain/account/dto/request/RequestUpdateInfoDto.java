package sharev.domain.account.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RequestUpdateInfoDto(
        @NotBlank
        @Size(max = 255)
        String name,

        @Email
        @Size(max = 320)
        String email
) {
}
