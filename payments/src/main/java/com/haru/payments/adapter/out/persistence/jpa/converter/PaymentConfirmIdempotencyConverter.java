package com.haru.payments.adapter.out.persistence.jpa.converter;

import com.haru.payments.adapter.out.persistence.jpa.entity.PaymentConfirmIdempotencyJpaEntity;
import com.haru.payments.domain.model.PaymentConfirmIdempotency;

public class PaymentConfirmIdempotencyConverter {
    public static PaymentConfirmIdempotency toDomain(PaymentConfirmIdempotencyJpaEntity entity) {
        return new PaymentConfirmIdempotency(
                entity.getId(),
                entity.getClientId(),
                entity.getIdempotencyKey(),
                entity.getPaymentId(),
                entity.getCreatedAt()
        );
    }

    public static PaymentConfirmIdempotencyJpaEntity toEntity(PaymentConfirmIdempotency domain) {
        return new PaymentConfirmIdempotencyJpaEntity(
                domain.getId(),
                domain.getClientId(),
                domain.getIdempotencyKey(),
                domain.getPaymentId(),
                domain.getCreatedAt()
        );
    }
}
