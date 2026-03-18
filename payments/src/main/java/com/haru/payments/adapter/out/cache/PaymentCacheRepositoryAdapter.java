package com.haru.payments.adapter.out.cache;

import com.haru.payments.application.cache.PaymentCacheRepository;
import com.haru.payments.application.dto.PaymentResponse;
import com.haru.payments.application.dto.RequestPaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentCacheRepositoryAdapter implements PaymentCacheRepository {
    private final CacheManager cacheManager;
    private static final String PROVISIONAL_PAYMENT_CACHE = "provisionalPayment";
    private static final String PROVISIONAL_PAYMENT_IDEMPOTENCY_CACHE = "provisionalPaymentIdempotency";

    @Override
    public Optional<RequestPaymentResponse> findPaymentRequestById(UUID id) {
        Cache cache = cacheManager.getCache("paymentRequest");
        RequestPaymentResponse cacheValue = cache.get(id, RequestPaymentResponse.class);

        return Optional.ofNullable(cacheValue);
    }

    @Override
    public Optional<PaymentResponse> findProvisionalPaymentById(UUID id) {
        Cache cache = cacheManager.getCache(PROVISIONAL_PAYMENT_CACHE);
        PaymentResponse cacheValue = cache.get(id, PaymentResponse.class);

        return Optional.ofNullable(cacheValue);
    }

    @Override
    public Optional<PaymentResponse> findProvisionalPaymentByIdempotency(UUID clientId, String idempotencyKey) {
        Cache cache = cacheManager.getCache(PROVISIONAL_PAYMENT_IDEMPOTENCY_CACHE);
        PaymentResponse cacheValue = cache.get(buildScopedKey(clientId, idempotencyKey), PaymentResponse.class);
        return Optional.ofNullable(cacheValue);
    }

    @Override
    public void saveProvisionalPayment(PaymentResponse paymentResponse) {
        Cache cache = cacheManager.getCache(PROVISIONAL_PAYMENT_CACHE);
        cache.put(paymentResponse.requestId(), paymentResponse);
    }

    @Override
    public void saveProvisionalPaymentByIdempotency(UUID clientId, String idempotencyKey, PaymentResponse paymentResponse) {
        Cache cache = cacheManager.getCache(PROVISIONAL_PAYMENT_IDEMPOTENCY_CACHE);
        cache.put(buildScopedKey(clientId, idempotencyKey), paymentResponse);
    }

    @Override
    public void evictProvisionalPayment(UUID id) {
        Cache cache = cacheManager.getCache(PROVISIONAL_PAYMENT_CACHE);
        cache.evict(id);
    }

    @Override
    public void evictProvisionalPaymentByIdempotency(UUID clientId, String idempotencyKey) {
        Cache cache = cacheManager.getCache(PROVISIONAL_PAYMENT_IDEMPOTENCY_CACHE);
        cache.evict(buildScopedKey(clientId, idempotencyKey));
    }

    private String buildScopedKey(UUID clientId, String idempotencyKey) {
        return clientId + ":" + idempotencyKey;
    }
}
