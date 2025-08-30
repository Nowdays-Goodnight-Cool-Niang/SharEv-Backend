package sharev.card_connection.dto.response;

import org.springframework.data.domain.Page;

public record ResponseConnectionInfoDto(
        Long registerCount,
        Page<ResponseConnectionCardDto> relationProfiles) {
}



