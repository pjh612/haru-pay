package com.haru.payments.application.usecase;

import com.haru.payments.application.dto.CompletePaymentRequest;

import java.util.UUID;

public interface ConfirmPaymentUseCase {
    void confirm(CompletePaymentRequest request);

    void failConfirm(UUID requestId);
}
