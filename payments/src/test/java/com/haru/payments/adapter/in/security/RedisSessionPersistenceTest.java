package com.haru.payments.adapter.in.security;

import com.haru.payments.support.ContainerizedIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.session.SessionRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RedisSessionPersistenceTest extends ContainerizedIntegrationTest {

    private static final String SESSION_NAMESPACE = SessionConfig.SESSION_NAMESPACE;
    private static final String SESSION_KEY_PREFIX = SESSION_NAMESPACE + ":sessions:";
    private static final String TEST_EMAIL = "test@harupay.com";
    private static final String TEST_PASSWORD = "test1234";

    @LocalServerPort
    private int port;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private SessionRepository<?> sessionRepository;

    @Test
    void login_ShouldPersistSpringSessionInRedis() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = login(client);

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(sessionRepository.getClass().getName()).contains("Redis");

        String encodedSessionId = response.headers()
                .firstValue("Set-Cookie")
                .map(this::extractSessionId)
                .orElseThrow();
        String sessionId = decodeSessionId(encodedSessionId);

        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            Set<String> sessionKeys = connection.keyCommands().keys(bytes(SESSION_KEY_PREFIX + "*")).stream()
                    .map(key -> new String(key, StandardCharsets.UTF_8))
                    .collect(Collectors.toSet());

            assertThat(sessionKeys).isNotEmpty();

            Map.Entry<String, Set<String>> storedSession = sessionKeys.stream()
                    .collect(Collectors.toMap(
                            key -> key,
                            key -> connection.hashCommands().hKeys(bytes(key)).stream()
                                    .map(field -> new String(field, StandardCharsets.UTF_8))
                                    .collect(Collectors.toSet())
                    ))
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().contains(
                            "sessionAttr:" + HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY))
                    .findFirst()
                    .orElseThrow();

            assertThat(storedSession.getKey()).startsWith(SESSION_KEY_PREFIX);
            assertThat(storedSession.getKey()).contains(sessionId);

            Set<String> hashFields = storedSession.getValue();

            assertThat(hashFields).contains(
                    "creationTime",
                    "lastAccessedTime",
                    "maxInactiveInterval",
                    "sessionAttr:" + HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY
            );

            Long ttlSeconds = connection.keyCommands().ttl(bytes(storedSession.getKey()));
            assertThat(ttlSeconds).isNotNull();
            assertThat(ttlSeconds).isPositive();
            assertThat(Duration.ofSeconds(ttlSeconds)).isLessThanOrEqualTo(Duration.ofMinutes(30));
        }
    }

    @Test
    void deletedDeveloperSession_ShouldReturnForbiddenWithoutRedirectingToPayerOidcFlow() throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
        Set<String> sessionKeysBeforeLogin = sessionKeys();

        HttpResponse<String> loginResponse = login(client);
        String encodedSessionId = loginResponse.headers()
                .firstValue("Set-Cookie")
                .map(this::extractSessionId)
                .orElseThrow();
        String deletedSessionId = decodeSessionId(encodedSessionId);
        String deletedSessionKey = SESSION_KEY_PREFIX + deletedSessionId;

        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            connection.keyCommands().del(bytes(deletedSessionKey));
        }

        HttpRequest protectedRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/clients/me"))
                .header("Cookie", SessionConfig.SESSION_COOKIE_NAME + "=" + encodedSessionId)
                .GET()
                .build();

        HttpResponse<String> refreshResponse = client.send(protectedRequest, HttpResponse.BodyHandlers.ofString());

        assertThat(refreshResponse.statusCode()).isEqualTo(403);
        assertThat(refreshResponse.headers().firstValue("Location")).isEmpty();

        Set<String> sessionKeysAfterRefresh = sessionKeys();
        Set<String> newSessionKeys = new HashSet<>(sessionKeysAfterRefresh);
        newSessionKeys.removeAll(sessionKeysBeforeLogin);

        assertThat(newSessionKeys).doesNotContain(deletedSessionKey);
    }

    @Test
    void logout_ShouldExpireSessionCookie() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> loginResponse = login(client);
        String encodedSessionId = loginResponse.headers()
                .firstValue("Set-Cookie")
                .map(this::extractSessionId)
                .orElseThrow();

        HttpRequest logoutRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/clients/logout"))
                .header("Cookie", SessionConfig.SESSION_COOKIE_NAME + "=" + encodedSessionId)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> logoutResponse = client.send(logoutRequest, HttpResponse.BodyHandlers.ofString());

        assertThat(logoutResponse.statusCode()).isEqualTo(200);
        assertThat(logoutResponse.headers().allValues("Set-Cookie"))
                .anyMatch(cookie -> cookie.contains(SessionConfig.SESSION_COOKIE_NAME + "=")
                        && cookie.contains("Max-Age=0"));
    }

    private static byte[] bytes(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }

    private HttpResponse<String> login(HttpClient client) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/api/clients/login"))
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString("""
                        {
                          "email": "%s",
                          "password": "%s"
                        }
                        """.formatted(TEST_EMAIL, TEST_PASSWORD)))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private Set<String> sessionKeys() {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            return connection.keyCommands().keys(bytes(SESSION_KEY_PREFIX + "*")).stream()
                    .map(key -> new String(key, StandardCharsets.UTF_8))
                    .collect(Collectors.toSet());
        }
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
}
