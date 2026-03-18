package com.haru.payments.adapter.out.cache;

import com.haru.payments.application.port.out.cache.EmailVerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EmailVerificationTokenAdapter implements EmailVerificationTokenRepository {
    private static final String KEY_PREFIX = "payments:email-verification:";
    private static final Duration TTL = Duration.ofHours(24);

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void save(String token, UUID clientId) {
        stringRedisTemplate.opsForValue().set(buildKey(token), clientId.toString(), TTL);
    }

    @Override
    public Optional<UUID> findClientIdByToken(String token) {
        String value = stringRedisTemplate.opsForValue().get(buildKey(token));
        return Optional.ofNullable(value).map(UUID::fromString);
    }

    @Override
    public void delete(String token) {
        stringRedisTemplate.delete(buildKey(token));
    }

    private String buildKey(String token) {
        return KEY_PREFIX + token;
    }
}
