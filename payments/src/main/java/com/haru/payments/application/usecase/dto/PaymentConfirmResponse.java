package com.haru.payments.application.usecase.dto;

import com.haru.payments.domain.model.PaymentRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentConfirmResponse(
        UUID requestId,
        String orderId,
        UUID requestMemberId,
        BigDecimal requestPrice,
        UUID clientId,
        int paymentStatus,
        Instant approvedAt
) {
    public static PaymentConfirmResponse of(PaymentRequest request) {
        return new PaymentConfirmResponse(
                request.getRequestId(),
                request.getOrderId(),
                request.getRequestMemberId(),
                request.getRequestPrice(),
                request.getClientId(),
                request.getPaymentStatus(),
                request.getCreatedAt()
        );
    }
}
