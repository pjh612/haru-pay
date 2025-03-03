package com.haru.banking.adapter.out.client.member;

import com.haru.banking.appliation.client.MemberClient;
import com.haru.banking.appliation.client.dto.FindMemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MemberClientAdapter implements MemberClient {
    private final RestClient memberRestClient;

    @Override
    public FindMemberResponse findByMemberId(UUID memberId) {
        try {
            return memberRestClient.get()
                    .uri("/api/members/{id}", memberId)
                    .retrieve()
                    .body(FindMemberResponse.class);
        } catch (Exception e) {
            return null;
        }
    }
}
