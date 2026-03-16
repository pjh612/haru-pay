package com.haru.testclient.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResultResponse(
        UUID requestId,
        String orderId,
        UUID requestMemberId,
        BigDecimal requestPrice,
        UUID clientId,
        int paymentStatus,
        Instant approvedAt
) {
}
