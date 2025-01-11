package org.example.backend.account.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record RequestUpdateInfoDto(
        @NotEmpty
        String name,

        String phone,

        @NotNull
        Integer profileImageId,

        String githubUrl,

        String instagramUrl,

        String facebookUrl
) {
}
