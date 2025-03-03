package com.haru.userapi.application;


import com.haru.userapi.application.dto.MemberSignupRequest;
import com.haru.userapi.application.dto.MemberSignupResponse;

public interface MemberSignupUseCase {
    MemberSignupResponse signup(MemberSignupRequest request);
}
