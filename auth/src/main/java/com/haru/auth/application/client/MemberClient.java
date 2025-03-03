package com.haru.auth.application.client;

import com.haru.auth.application.client.dto.MemberAuthResponse;

public interface MemberClient {
    MemberAuthResponse auth(String id, String password);
}
