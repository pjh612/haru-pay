package com.haru.payments.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentConfirmIdempotency {
    private UUID id;
    private UUID clientId;
    private String idempotencyKey;
    private UUID paymentId;
    private Instant createdAt;

    public static PaymentConfirmIdempotency createNew(UUID clientId, String idempotencyKey, UUID paymentId) {
        return new PaymentConfirmIdempotency(null, clientId, idempotencyKey, paymentId, Instant.now());
    }
}
