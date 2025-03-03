package com.haru.userapi.application;

import com.haru.userapi.application.dto.MemberAuthResponse;

public interface MemberAuthUseCase {
    MemberAuthResponse auth(String id, String password);
}
