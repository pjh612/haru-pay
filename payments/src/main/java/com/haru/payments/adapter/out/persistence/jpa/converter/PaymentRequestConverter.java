package com.haru.payments.adapter.out.persistence.jpa.converter;

import com.haru.payments.adapter.out.persistence.jpa.entity.PaymentRequestJpaEntity;
import com.haru.payments.domain.model.PaymentRequest;

public class PaymentRequestConverter {
    public static PaymentRequest toDomain(PaymentRequestJpaEntity entity) {
        return new PaymentRequest(
                entity.getRequestId(),
                entity.getRequestMemberId(),
                entity.getRequestPrice(),
                entity.getClientId(),
                entity.getPaymentStatus(),
                entity.getApprovedAt(),
                entity.getCreatedAt()
        );
    }

    public static PaymentRequestJpaEntity toEntity(PaymentRequest domain) {
        return new PaymentRequestJpaEntity(
                domain.getRequestId(),
                domain.getRequestMemberId(),
                domain.getRequestPrice(),
                domain.getClientId(),
                domain.getPaymentStatus(),
                domain.getApprovedAt(),
                domain.getCreatedAt()
        );
    }
}
