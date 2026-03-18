package com.haru.payments.application.usecase;

import com.haru.payments.application.dto.PaymentResponse;

import java.util.UUID;

public interface QueryPaymentStatusUseCase {
    PaymentResponse query(UUID paymentId, UUID clientId);
}
