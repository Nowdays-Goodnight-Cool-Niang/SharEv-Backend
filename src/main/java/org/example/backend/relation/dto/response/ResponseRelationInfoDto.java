package org.example.backend.relation.dto.response;

import org.springframework.data.domain.Page;

public record ResponseRelationInfoDto(
        Long registerCount,
        Page<ResponseRelationProfileDto> relationProfiles) {
}



