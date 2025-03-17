package com.haru.payments.application.usecase.dto;

import com.haru.payments.adapter.in.event.PaymentConfirmRequestedEvent;
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
        String failureReason,
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
                null,
                request.getCreatedAt()
        );
    }

    public static PaymentConfirmResponse of(PaymentConfirmRequestedEvent event) {
        return new PaymentConfirmResponse(
                event.getRequestId(),
                null,
                event.getRequestMemberId(),
                event.getRequestPrice(),
                null,
                "SUCCEEDED".equals(event.getType()) ? 1 : -1,
                event.getFailureReason(),
                null
        );
    }
}
