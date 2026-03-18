package com.haru.payments.adapter.in.security;

import com.haru.payments.domain.model.Client;
import com.haru.payments.domain.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@RequiredArgsConstructor
public class ClientEmailPasswordAuthenticationProvider implements AuthenticationProvider {
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = (String) authentication.getCredentials();

        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("클라이언트를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(password, client.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        if (!client.isActive()) {
            throw new DisabledException("비활성화된 계정입니다.");
        }

        if (!client.isEmailVerified()) {
            throw new DisabledException("이메일 인증이 필요합니다.");
        }

        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_CLIENT")
        );

        return new ClientEmailPasswordAuthenticationToken(client, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ClientEmailPasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
