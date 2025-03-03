package com.haru.payments.application.usecase;

import com.haru.payments.application.dto.*;

import java.util.UUID;

public interface RequestPaymentUseCase {
    void confirmPayment(PaymentCommand request);

    PaymentResponse requestPayment(CreatePaymentRequest request);

    void failRequest(UUID requestId);

    RequestPaymentResponse requestPayment(RequestPaymentCommand command);

    PaymentResponse preparePayment(PreparePaymentCommand request);
}
