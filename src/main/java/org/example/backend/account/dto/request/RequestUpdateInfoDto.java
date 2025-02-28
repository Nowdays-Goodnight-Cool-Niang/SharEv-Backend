package org.example.backend.account.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record RequestUpdateInfoDto(
        @NotEmpty
        String name,

        @Email
        String email,

        String linkedinUrl,

        String githubUrl,

        String instagramUrl
) {
}
