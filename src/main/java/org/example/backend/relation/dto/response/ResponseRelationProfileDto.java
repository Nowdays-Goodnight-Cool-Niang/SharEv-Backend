package org.example.backend.relation.dto.response;

import org.example.backend.profile.dto.response.ResponseProfileDto;
import org.springframework.data.domain.Page;

public record ResponseRelationProfileDto(
        Long registerCount,
        Page<ResponseProfileDto> relationProfiles) {
}



