package com.haru.banking.appliation.client;

import com.haru.banking.appliation.client.dto.FindMemberResponse;

import java.util.UUID;

public interface MemberClient {
    FindMemberResponse findByMemberId(UUID memberId);
}
