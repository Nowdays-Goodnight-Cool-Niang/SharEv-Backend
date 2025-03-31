package org.example.backend.account.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.example.backend.valid.GithubUrl;
import org.example.backend.valid.InstagramUrl;
import org.example.backend.valid.LinkedinUrl;

public record RequestUpdateInfoDto(
        @NotEmpty
        String name,

        @Email
        String email,

        @LinkedinUrl
        String linkedinUrl,

        @GithubUrl
        String githubUrl,

        @InstagramUrl
        String instagramUrl
) {
}
