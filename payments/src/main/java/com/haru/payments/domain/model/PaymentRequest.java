package com.haru.payments.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentRequest {
    private UUID requestId;
    private UUID requestMemberId;
    private BigDecimal requestPrice;
    private UUID clientId;
    private int paymentStatus;
    private Instant approvedAt;
    private Instant createdAt;

    public static PaymentRequest createNew(UUID requestId, UUID requestMemberId, BigDecimal requestPrice, UUID clientId) {
        return new PaymentRequest(requestId, requestMemberId, requestPrice, clientId, 0, null, Instant.now());
    }

    public void success() {
        this.paymentStatus = 1;
        this.approvedAt = Instant.now();
    }

    public void fail() {
        this.paymentStatus = -1;
        this.approvedAt = null;
    }
}
