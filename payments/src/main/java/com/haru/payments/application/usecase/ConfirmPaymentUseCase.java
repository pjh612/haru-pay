package com.haru.payments.application.usecase;

import com.haru.payments.application.dto.CompletePaymentRequest;
import com.haru.payments.application.usecase.dto.PaymentConfirmResponse;

import java.util.UUID;

public interface ConfirmPaymentUseCase {
    PaymentConfirmResponse confirm(CompletePaymentRequest request);

    void failConfirm(UUID requestId);
}
