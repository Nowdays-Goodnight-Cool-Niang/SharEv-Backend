package org.example.backend.account.dto.response;

import lombok.Getter;

import java.util.UUID;

@Getter
public record ResponseAccountIdDto(UUID accountId) {
}
