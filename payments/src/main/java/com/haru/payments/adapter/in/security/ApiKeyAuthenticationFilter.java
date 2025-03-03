package com.haru.payments.adapter.in.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationManager authenticationManager;

    public ApiKeyAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String authorization = request.getHeader("Authorization");
        String clientId = request.getHeader("X-PAY-CLIENT-ID");
        ApiKeyAuthenticationToken authentication = new ApiKeyAuthenticationToken(authorization, clientId, null);

        return this.authenticationManager.authenticate(authentication);
    }

    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String authorization = request.getHeader("Authorization");
        String clientId = request.getHeader("X-PAY-CLIENT-ID");
        return authorization != null && clientId != null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (requiresAuthentication(request, response)) {
            Authentication authentication = attemptAuthentication(request, response);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}
