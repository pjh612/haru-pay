package com.haru.testclient.application.dto;

import java.time.Instant;
import java.util.UUID;

public record ClientResponse(
        UUID id,
        String name,
        String apiKey,
        boolean isActive,
        Instant createdAt
) {
}
