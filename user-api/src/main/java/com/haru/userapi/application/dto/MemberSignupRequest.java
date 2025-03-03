package com.haru.userapi.application.dto;

import com.haru.userapi.domain.models.Gender;
import com.haru.userapi.domain.models.Member;

public record MemberSignupRequest(
        String username,
        String password,
        String name,
        Gender gender
) {
    public Member toEntity() {
        return Member.creatNew(username, password, name, gender);
    }
}
