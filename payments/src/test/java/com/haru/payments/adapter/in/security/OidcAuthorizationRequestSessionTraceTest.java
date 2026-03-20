package com.haru.payments.adapter.in.security;

import com.haru.payments.support.ContainerizedIntegrationTest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.session.web.http.SessionRepositoryFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OidcAuthorizationRequestSessionTraceTest extends ContainerizedIntegrationTest {

    private static final String AUTHORIZATION_REQUEST_ATTRIBUTE_NAME = authorizationRequestAttributeName();
    private static final String SESSION_KEY_PREFIX = SessionConfig.SESSION_NAMESPACE + ":sessions:";

    @LocalServerPort
    private int port;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private RedisSerializer<Object> springSessionDefaultRedisSerializer;

    @Autowired
    private CallbackTraceRecorder callbackTraceRecorder;

    @Test
    void shouldTraceAuthorizationRequestSessionAcrossRedirectAndCallback() throws Exception {
        callbackTraceRecorder.reset();

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();

        HttpResponse<String> authorizationResponse = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/oauth2/authorization/payments-oidc"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertThat(authorizationResponse.statusCode()).isEqualTo(302);

        String encodedSessionId = authorizationResponse.headers()
                .firstValue("Set-Cookie")
                .map(this::extractSessionId)
                .orElseThrow();
        String sessionId = decodeSessionId(encodedSessionId);
        String sessionKey = SESSION_KEY_PREFIX + sessionId;

        OAuth2AuthorizationRequest storedAuthorizationRequest;
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            byte[] serializedAuthorizationRequest = connection.hashCommands().hGet(
                    bytes(sessionKey),
                    bytes("sessionAttr:" + AUTHORIZATION_REQUEST_ATTRIBUTE_NAME)
            );

            assertThat(serializedAuthorizationRequest).isNotNull();

            storedAuthorizationRequest = (OAuth2AuthorizationRequest) springSessionDefaultRedisSerializer
                    .deserialize(serializedAuthorizationRequest);
        }

        assertThat(storedAuthorizationRequest).isNotNull();
        assertThat(storedAuthorizationRequest.getAttributes()).containsEntry("registration_id", "payments-oidc");

        HttpResponse<String> callbackResponse = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:" + port + "/login/oauth2/code/payments-oidc?code=dummy-code&state="
                                + storedAuthorizationRequest.getState()))
                        .header("Cookie", SessionConfig.SESSION_COOKIE_NAME + "=" + encodedSessionId)
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertThat(callbackResponse.statusCode()).isEqualTo(401);
        assertThat(callbackTraceRecorder.callbackSessionId()).isEqualTo(sessionId);
        assertThat(callbackTraceRecorder.authorizationRequestPresent()).isTrue();
    }

    private static byte[] bytes(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }

    private String extractSessionId(String setCookieHeader) {
        for (String cookie : setCookieHeader.split(";")) {
            String trimmed = cookie.trim();
            if (trimmed.startsWith(SessionConfig.SESSION_COOKIE_NAME + "=")) {
                return trimmed.substring((SessionConfig.SESSION_COOKIE_NAME + "=").length());
            }
        }
        throw new IllegalStateException(SessionConfig.SESSION_COOKIE_NAME + " cookie not found: " + setCookieHeader);
    }

    private String decodeSessionId(String encodedSessionId) {
        return new String(Base64.getDecoder().decode(encodedSessionId), StandardCharsets.UTF_8);
    }

    private static String authorizationRequestAttributeName() {
        try {
            Field field = HttpSessionOAuth2AuthorizationRequestRepository.class
                    .getDeclaredField("DEFAULT_AUTHORIZATION_REQUEST_ATTR_NAME");
            field.setAccessible(true);
            return (String) field.get(null);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to resolve authorization request attribute name", exception);
        }
    }

    static final class CallbackTraceRecorder {
        private volatile String callbackSessionId;
        private volatile boolean authorizationRequestPresent;

        void reset() {
            this.callbackSessionId = null;
            this.authorizationRequestPresent = false;
        }

        void record(String callbackSessionId, boolean authorizationRequestPresent) {
            this.callbackSessionId = callbackSessionId;
            this.authorizationRequestPresent = authorizationRequestPresent;
        }

        String callbackSessionId() {
            return callbackSessionId;
        }

        boolean authorizationRequestPresent() {
            return authorizationRequestPresent;
        }
    }

    @TestConfiguration
    static class CallbackTraceConfiguration {
        @Bean
        CallbackTraceRecorder callbackTraceRecorder() {
            return new CallbackTraceRecorder();
        }

        @Bean
        FilterRegistrationBean<OncePerRequestFilter> callbackTraceFilter(CallbackTraceRecorder recorder) {
            FilterRegistrationBean<OncePerRequestFilter> registration = new FilterRegistrationBean<>();
            registration.setFilter(new OncePerRequestFilter() {
                @Override
                protected void doFilterInternal(HttpServletRequest request,
                                                HttpServletResponse response,
                                                FilterChain filterChain) throws ServletException, IOException {
                    if (request.getServletPath().startsWith("/login/oauth2/code/")) {
                        var session = request.getSession(false);
                        recorder.record(
                                session != null ? session.getId() : null,
                                session != null && session.getAttribute(AUTHORIZATION_REQUEST_ATTRIBUTE_NAME) != null
                        );
                    }

                    filterChain.doFilter(request, response);
                }
            });
            registration.setOrder(SessionRepositoryFilter.DEFAULT_ORDER + 1);
            return registration;
        }
    }
}
