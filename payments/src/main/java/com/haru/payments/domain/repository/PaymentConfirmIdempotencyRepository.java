package com.haru.payments.domain.repository;

import com.haru.payments.domain.model.PaymentConfirmIdempotency;

import java.util.Optional;
import java.util.UUID;

public interface PaymentConfirmIdempotencyRepository {
    PaymentConfirmIdempotency save(PaymentConfirmIdempotency paymentConfirmIdempotency);

    Optional<PaymentConfirmIdempotency> findByClientIdAndIdempotencyKey(UUID clientId, String idempotencyKey);
}
