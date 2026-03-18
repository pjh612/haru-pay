package com.haru.payments.application.usecase.impl;

import com.haru.payments.application.dto.PaymentResponse;
import com.haru.payments.application.usecase.QueryPaymentStatusUseCase;
import com.haru.payments.domain.model.PaymentRequest;
import com.haru.payments.domain.repository.PaymentRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QueryPaymentStatusService implements QueryPaymentStatusUseCase {
    private final PaymentRequestRepository paymentRequestRepository;

    @Override
    public PaymentResponse query(UUID paymentId, UUID clientId) {
        PaymentRequest paymentRequest = paymentRequestRepository.findByIdAndClientId(paymentId, clientId)
                .orElseThrow(() -> new EntityNotFoundException("결제 정보를 찾을 수 없습니다."));
        return PaymentResponse.of(paymentRequest);
    }
}
