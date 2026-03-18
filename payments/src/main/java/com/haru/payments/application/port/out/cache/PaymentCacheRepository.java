package com.haru.payments.application.port.out.cache;

import com.haru.payments.application.dto.PaymentResponse;
import com.haru.payments.application.dto.RequestPaymentResponse;

import java.util.Optional;
import java.util.UUID;

public interface PaymentCacheRepository {

    Optional<RequestPaymentResponse> findPaymentRequestById(UUID id);

    Optional<PaymentResponse> findProvisionalPaymentById(UUID id);

    Optional<PaymentResponse> findProvisionalPaymentByIdempotency(UUID clientId, String idempotencyKey);

    void saveProvisionalPayment(PaymentResponse paymentResponse);

    void saveProvisionalPaymentByIdempotency(UUID clientId, String idempotencyKey, PaymentResponse paymentResponse);

    void evictProvisionalPayment(UUID id);

    void evictProvisionalPaymentByIdempotency(UUID clientId, String idempotencyKey);
}
