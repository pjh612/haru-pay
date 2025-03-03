package com.haru.payments.domain.repository;

import com.haru.payments.domain.model.PaymentRequest;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRequestRepository {
    PaymentRequest save(PaymentRequest paymentRequest);

    Optional<PaymentRequest> findById(UUID paymentRequestId);
}
