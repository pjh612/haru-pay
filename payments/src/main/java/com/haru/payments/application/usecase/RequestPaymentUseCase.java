package com.haru.payments.application.usecase;

import com.haru.payments.application.dto.*;

public interface RequestPaymentUseCase {
    void confirmPayment(PaymentCommand request);

    RequestPaymentResponse requestPayment(RequestPaymentCommand command);

    PaymentResponse preparePayment(PreparePaymentCommand request);
}
