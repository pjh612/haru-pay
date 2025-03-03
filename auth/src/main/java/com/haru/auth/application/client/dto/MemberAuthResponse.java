package com.haru.auth.application.client.dto;

import java.util.UUID;

public record MemberAuthResponse(
        UUID memberId,
        String name
) {
}
