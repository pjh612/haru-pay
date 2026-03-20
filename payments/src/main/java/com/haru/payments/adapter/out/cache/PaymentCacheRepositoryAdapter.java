package com.haru.payments.adapter.out.cache;

import com.haru.payments.application.port.out.cache.PaymentCacheRepository;
import com.haru.payments.application.dto.PaymentResponse;
import com.haru.payments.application.dto.RequestPaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCacheRepositoryAdapter implements PaymentCacheRepository {

    private static final String PROVISIONAL_KEY_PREFIX = "provisionalPayment::";
    private static final String IDEMPOTENCY_KEY_PREFIX = "provisionalPaymentIdempotency::";
    private static final Duration PROVISIONAL_TTL = Duration.ofMinutes(10);

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<PaymentResponse> findProvisionalPaymentById(UUID id) {
        String json = stringRedisTemplate.opsForValue().get(PROVISIONAL_KEY_PREFIX + id);
        if (json == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(json, PaymentResponse.class));
        } catch (Exception e) {
            log.error("[cache] Failed to deserialize PaymentResponse for id={}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<PaymentResponse> findProvisionalPaymentByIdempotency(UUID clientId, String idempotencyKey) {
        String json = stringRedisTemplate.opsForValue().get(buildIdempotencyKey(clientId, idempotencyKey));
        if (json == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(json, PaymentResponse.class));
        } catch (Exception e) {
            log.error("[cache] Failed to deserialize PaymentResponse for idempotencyKey={}: {}", idempotencyKey, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void saveProvisionalPayment(PaymentResponse paymentResponse) {
        try {
            String json = objectMapper.writeValueAsString(paymentResponse);
            stringRedisTemplate.opsForValue().set(PROVISIONAL_KEY_PREFIX + paymentResponse.requestId(), json, PROVISIONAL_TTL);
        } catch (Exception e) {
            throw new RuntimeException("가결제 저장 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public void saveProvisionalPaymentByIdempotency(UUID clientId, String idempotencyKey, PaymentResponse paymentResponse) {
        try {
            String json = objectMapper.writeValueAsString(paymentResponse);
            stringRedisTemplate.opsForValue().set(buildIdempotencyKey(clientId, idempotencyKey), json, PROVISIONAL_TTL);
        } catch (Exception e) {
            throw new RuntimeException("멱등성 가결제 저장 실패: " + e.getMessage(), e);
        }
    }

    @Override
    public void evictProvisionalPayment(UUID id) {
        stringRedisTemplate.delete(PROVISIONAL_KEY_PREFIX + id);
    }

    @Override
    public void evictProvisionalPaymentByIdempotency(UUID clientId, String idempotencyKey) {
        stringRedisTemplate.delete(buildIdempotencyKey(clientId, idempotencyKey));
    }

    private String buildIdempotencyKey(UUID clientId, String idempotencyKey) {
        return IDEMPOTENCY_KEY_PREFIX + clientId + ":" + idempotencyKey;
    }
}
