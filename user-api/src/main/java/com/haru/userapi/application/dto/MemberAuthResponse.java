package com.haru.userapi.application.dto;

import java.util.UUID;

public record MemberAuthResponse(
        UUID memberId,
        String name
) {
}
