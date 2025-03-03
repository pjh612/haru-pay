package com.haru.payments.adapter.out.cache;

import com.haru.payments.application.cache.PaymentCacheRepository;
import com.haru.payments.application.dto.PaymentResponse;
import com.haru.payments.application.dto.RequestPaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentCacheRepositoryAdapter implements PaymentCacheRepository {
    private final CacheManager cacheManager;

    @Override
    public Optional<RequestPaymentResponse> findPaymentRequestById(UUID id) {
        Cache cache = cacheManager.getCache("paymentRequest");
        RequestPaymentResponse cacheValue = cache.get(id, RequestPaymentResponse.class);

        return Optional.ofNullable(cacheValue);
    }

    @Override
    public Optional<PaymentResponse> findProvisionalPaymentById(UUID id) {
        Cache cache = cacheManager.getCache("provisionalPayment");
        PaymentResponse cacheValue = cache.get(id, PaymentResponse.class);

        return Optional.ofNullable(cacheValue);
    }

}
