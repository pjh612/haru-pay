package com.haru.payments.application.usecase.impl;

import com.haru.payments.application.cache.PaymentCacheRepository;
import com.haru.payments.application.dto.PaymentResponse;
import com.haru.payments.application.usecase.QueryPaymentUseCase;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QueryPaymentCacheService implements QueryPaymentUseCase {
    private final PaymentCacheRepository cacheRepository;

    @Override
    public PaymentResponse queryById(UUID requestId) {
        return cacheRepository.findProvisionalPaymentById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("결제 정보를 찾을 수 없습니다."));
    }
}
