package org.example.backend.profile.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RequestUpdateProfileInfoDto(
        @NotBlank
        @Size(max = 255)
        String introduce,

        @NotBlank
        @Size(max = 255)
        String proudestExperience,

        @NotBlank
        @Size(max = 255)
        String toughExperience
) {
}
