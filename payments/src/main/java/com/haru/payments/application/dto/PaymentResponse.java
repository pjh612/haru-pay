package com.haru.payments.application.dto;

import com.haru.payments.domain.model.PaymentRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(UUID requestId,
                              UUID requestMemberId,
                              BigDecimal requestPrice,
                              UUID clientId,
                              int paymentStatus,
                              Instant approvedAt) {

    public static PaymentResponse of(PaymentRequest paymentRequest) {
        return new PaymentResponse(
                paymentRequest.getRequestId(),
                paymentRequest.getRequestMemberId(),
                paymentRequest.getRequestPrice(),
                paymentRequest.getClientId(),
                paymentRequest.getPaymentStatus(),
                paymentRequest.getApprovedAt()
        );
    }
}
