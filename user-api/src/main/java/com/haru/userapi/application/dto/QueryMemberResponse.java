package com.haru.userapi.application.dto;


import com.haru.userapi.domain.models.Gender;

import java.util.UUID;

public record QueryMemberResponse(
        UUID id,
        String username,
        String password,
        String name,
        Gender gender
) {
}
