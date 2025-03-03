package com.haru.payments.application.dto;

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
