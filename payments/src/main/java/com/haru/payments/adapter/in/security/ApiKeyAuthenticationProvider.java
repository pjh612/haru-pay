package com.haru.payments.adapter.in.security;

import com.haru.payments.domain.model.Client;
import com.haru.payments.domain.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
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
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String apiKey = (String) authentication.getCredentials();
        String clientId = authentication.getName();

        if (apiKey == null) {
            throw new BadCredentialsException("API Key가 필요합니다.");
        }

        Client client = clientRepository.findById(UUID.fromString(clientId))
                .orElseThrow(() -> new BadCredentialsException("클라이언트를 찾을 수 없습니다."));

        if (!client.isActive()) {
            throw new DisabledException("비활성화된 클라이언트입니다.");
        }

        if (!passwordEncoder.matches(apiKey, client.getApiKey())) {
            throw new BadCredentialsException("API Key가 일치하지 않습니다.");
        }

        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_PAYMENT_CLIENT"));
        authorities.add(new SimpleGrantedAuthority("PAYMENT_PREPARE"));
        authorities.add(new SimpleGrantedAuthority("PAYMENT_CONFIRM"));

        return new ApiKeyAuthenticationToken(apiKey, clientId, client, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
