package com.haru.payments.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentRequest(
        UUID requestId,
        String orderId,
        UUID requestMemberId,
        String productName,
        BigDecimal requestPrice,
        UUID clientId
) {
}
