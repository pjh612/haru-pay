package com.haru.payments.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentRequest(
        UUID requestId,
        UUID requestMemberId,
        BigDecimal requestPrice,
        UUID clientId
) {
}
