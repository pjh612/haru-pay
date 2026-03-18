package com.haru.payments.application.dto;

import java.time.Instant;
import java.util.UUID;

public record ClientLoginResponse(
        UUID id,
        String name,
        boolean isActive,
        Instant createdAt
) {
}
