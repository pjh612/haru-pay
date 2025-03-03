package com.haru.payments.application.client;

import com.haru.payments.application.client.dto.MemberResponse;

import java.util.UUID;

public interface MemberClient {
    MemberResponse getMemberById(UUID memberId);
}
