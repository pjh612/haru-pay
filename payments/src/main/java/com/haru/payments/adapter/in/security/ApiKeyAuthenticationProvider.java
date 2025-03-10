package com.haru.payments.adapter.in.security;

import com.haru.payments.application.dto.ClientResponse;
import com.haru.payments.application.usecase.QueryClientUseCase;
import com.haru.payments.domain.model.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {
    private final QueryClientUseCase queryClientUseCase;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String apiKey = (String) authentication.getCredentials();
        String clientId = authentication.getName();
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();

        ClientResponse client = queryClientUseCase.queryById(UUID.fromString(clientId));
        if (!client.isActive()) {
            throw new RuntimeException("client is not active");
        }

        authorities.add(new SimpleGrantedAuthority("ROLE_PAYMENT_CLIENT"));
        authorities.add(new SimpleGrantedAuthority("PAYMENT_PREPARE"));
        if (apiKey!= null && passwordEncoder.matches(apiKey, client.apiKey())) {
            authorities.add(new SimpleGrantedAuthority("PAYMENT_CONFIRM"));
        }

        return new ApiKeyAuthenticationToken(apiKey, clientId, new Client(client.id(), client.name(), client.apiKey(), true, client.createdAt()), authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
