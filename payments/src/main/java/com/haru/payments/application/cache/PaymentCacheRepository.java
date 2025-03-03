package com.haru.payments.application.cache;

import com.haru.payments.application.dto.PaymentResponse;
import com.haru.payments.application.dto.RequestPaymentResponse;

import java.util.Optional;
import java.util.UUID;

public interface PaymentCacheRepository {

    Optional<RequestPaymentResponse> findPaymentRequestById(UUID id);

    Optional<PaymentResponse> findProvisionalPaymentById(UUID id);
}
