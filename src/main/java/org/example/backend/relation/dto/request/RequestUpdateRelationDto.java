package org.example.backend.relation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RequestUpdateRelationDto(
        @NotNull
        @Positive
        Integer targetPinNumber
) {
}
