package com.haru.testclient.adapter.in.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class JsonLoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final RequestMatcher DEFAULT_REQUEST_MATCHER =
            request -> "/demo/api/login".equals(request.getServletPath())
                    && "POST".equalsIgnoreCase(request.getMethod());
    private static final JsonParser JSON_PARSER = JsonParserFactory.getJsonParser();

    private final AuthenticationManager authenticationManager;

    public JsonLoginAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(DEFAULT_REQUEST_MATCHER);
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        if (!isJsonRequest(request)) {
            throw new AuthenticationServiceException("JSON 형식의 로그인 요청만 허용됩니다.");
        }

        String body;
        try {
            body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new AuthenticationServiceException("로그인 요청 본문을 읽을 수 없습니다.", e);
        }

        if (body == null || body.isBlank()) {
            throw new AuthenticationServiceException("로그인 요청 본문이 비어 있습니다.");
        }

        String username;
        String password;
        try {
            Map<String, Object> payload = JSON_PARSER.parseMap(body);
            username = String.valueOf(payload.getOrDefault("username", ""));
            password = String.valueOf(payload.getOrDefault("password", ""));
        } catch (RuntimeException e) {
            throw new AuthenticationServiceException("로그인 요청 JSON 형식이 올바르지 않습니다.", e);
        }

        if (username.isBlank() || password.isBlank()) {
            throw new AuthenticationServiceException("username, password는 필수입니다.");
        }

        return authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(username, password));
    }

    private boolean isJsonRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.toLowerCase().contains("application/json");
    }
}
