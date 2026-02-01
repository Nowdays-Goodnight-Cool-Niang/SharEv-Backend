package sharev.domain.team.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record RequestCreateTeamDto(
        @NotEmpty String title
) {
}
