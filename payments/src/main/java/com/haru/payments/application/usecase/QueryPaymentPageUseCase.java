package com.haru.payments.application.usecase;

import com.haru.payments.application.dto.PaymentPageResponse;

import java.util.UUID;

public interface QueryPaymentPageUseCase {
    PaymentPageResponse query(UUID paymentRequestId, UUID memberId);
}
