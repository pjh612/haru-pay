package com.haru.payments.adapter.in.security;

import com.haru.payments.domain.model.Client;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@Getter
public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {
    private final String apiKey;
    private final String clientId;
    private final Client client;

    public ApiKeyAuthenticationToken(String apiKey, String clientId, Client client) {
        super(null);
        this.apiKey = apiKey;
        this.clientId = clientId;
        this.client = client;
        setAuthenticated(client != null); // 인증 상태 설정
    }

    @Override
    public Object getCredentials() {
        return apiKey;
    }

    @Override
    public Object getPrincipal() {
        return client;
    }

    @Override
    public String getName() {
        return this.clientId;
    }
}
