package com.haru.payments.adapter.in.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationManager authenticationManager;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public ApiKeyAuthenticationFilter(AuthenticationManager authenticationManager,
                                      AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationManager = authenticationManager;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        String clientId = request.getHeader("X-PAY-CLIENT-ID");

        boolean isApiKeyRequest = authorization != null && clientId != null;

        if (isApiKeyRequest) {
            try {
                ApiKeyAuthenticationToken token = new ApiKeyAuthenticationToken(authorization, clientId, null, null);
                Authentication authentication = authenticationManager.authenticate(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (AuthenticationException e) {
                SecurityContextHolder.clearContext();
                authenticationEntryPoint.commence(request, response, e);
                return;
            }
        }

        if (request.getMethod().equalsIgnoreCase("OPTIONS") || isApiKeyRequest) {
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Headers", "X-PAY-CLIENT-ID, Authorization, Idempotency-Key, content-type");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        }

        filterChain.doFilter(request, response);
    }
}
