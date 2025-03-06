package com.haru.payments.application.usecase.impl;

import com.haru.payments.application.dto.CompletePaymentRequest;
import com.haru.payments.application.usecase.ConfirmPaymentUseCase;
import com.haru.payments.application.usecase.dto.PaymentConfirmResponse;
import com.haru.payments.domain.model.PaymentRequest;
import com.haru.payments.domain.repository.PaymentRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfirmPaymentService implements ConfirmPaymentUseCase {
    private final PaymentRequestRepository repository;

    @Override
    @Transactional
    public PaymentConfirmResponse confirm(CompletePaymentRequest request) {
        PaymentRequest foundRequest = repository.findById(request.requestId())
                .orElseThrow(() -> new EntityNotFoundException("결제 요청을 찾을 수 없습니다."));
        foundRequest.success();

        return PaymentConfirmResponse.of(repository.save(foundRequest));
    }

    @Override
    @Transactional
    public void failConfirm(UUID requestId) {
        PaymentRequest paymentRequest = repository.findById(requestId)
                .orElse(null);
        if (paymentRequest == null) {
            return;
        }

        paymentRequest.fail();

        repository.save(paymentRequest);

    }
}
