package com.haru.auth.adapter.out.client;

import com.haru.auth.application.client.MemberClient;
import com.haru.auth.application.client.dto.MemberAuthRequest;
import com.haru.auth.application.client.dto.MemberAuthResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class MemberClientAdapter implements MemberClient {
    private final RestClient restClient;

    public MemberClientAdapter(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public MemberAuthResponse auth(String id, String password) {
        return restClient.post()
                .uri("/api/member/authentication")
                .body(new MemberAuthRequest(id, password))
                .retrieve()
                .body(MemberAuthResponse.class);
    }
}
