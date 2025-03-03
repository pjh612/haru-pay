package com.haru.payments.adapter.in.security;

import com.haru.payments.application.dto.ClientResponse;
import com.haru.payments.application.usecase.QueryClientUseCase;
import com.haru.payments.domain.model.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@RequiredArgsConstructor
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {
    private final QueryClientUseCase queryClientUseCase;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String apiKey = (String) authentication.getCredentials();
        String clientId = authentication.getName();

        ClientResponse client = queryClientUseCase.queryById(UUID.fromString(clientId));
        if (!client.isActive()) {
            throw new RuntimeException("client is not active");
        }
        if (!passwordEncoder.matches(apiKey, client.apiKey())) {
            throw new RuntimeException("not valid api key");
        }

        return new ApiKeyAuthenticationToken(apiKey, clientId, new Client(client.id(), client.name(), client.apiKey(), true, client.createdAt()));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
