package com.haru.payments.adapter.out.client;

import com.haru.payments.application.client.MemberClient;
import com.haru.payments.application.client.dto.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class MemberClientAdapter implements MemberClient {
    private final RestClient memberRestClient;

    public MemberClientAdapter(@Qualifier("memberRestClient") RestClient memberRestClient) {
        this.memberRestClient = memberRestClient;
    }

    @Override
    public MemberResponse getMemberById(UUID memberId) {
        return memberRestClient.get()
                .uri("/api/members/{memberId}", memberId)
                .retrieve()
                .body(MemberResponse.class);
    }
}
