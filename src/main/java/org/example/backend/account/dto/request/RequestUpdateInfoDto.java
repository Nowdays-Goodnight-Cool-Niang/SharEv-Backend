package org.example.backend.account.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record RequestUpdateInfoDto(
        @NotBlank
        @Size(max = 255)
        String name,

        @Email
        @Size(max = 320)
        String email,

        @URL
        @Size(max = 255)
        String linkedinUrl,

        @URL
        @Size(max = 255)
        String githubUrl,

        @URL
        @Size(max = 255)
        String instagramUrl
) {
}
