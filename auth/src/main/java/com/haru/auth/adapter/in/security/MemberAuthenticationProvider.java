package com.haru.auth.adapter.in.security;

import com.haru.auth.application.client.dto.MemberAuthResponse;
import com.haru.auth.application.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;

@Slf4j
@RequiredArgsConstructor
public class MemberAuthenticationProvider implements AuthenticationProvider {
    private final MemberService memberService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        MemberAuthResponse auth;
        try {
            auth = memberService.auth(username, password);
        } catch (HttpClientErrorException e) {
            log.debug("Authentication failed for user '{}': {}", username, e.getStatusCode());
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
        } catch (Exception e) {
            log.error("Authentication service unavailable: {}", e.getMessage());
            throw new AuthenticationServiceException("인증 서비스에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.");
        }

        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_MEMBER"));
        MemberPrincipal principal = new MemberPrincipal(
                auth.memberId(),
                username,
                authorities
        );
        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
