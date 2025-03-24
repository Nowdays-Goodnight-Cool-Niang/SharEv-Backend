package org.example.backend.socialDex.dto.response;

import java.util.UUID;

public record ResponseSocialDexDto(UUID firstAccountId, UUID secondAccountId) {
}
