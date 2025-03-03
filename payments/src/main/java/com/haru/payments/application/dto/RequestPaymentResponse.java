package com.haru.payments.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RequestPaymentResponse(UUID requestId,
                                     UUID requestMemberId,
                                     BigDecimal requestPrice,
                                     UUID clientId,
                                     int paymentStatus,
                                     Instant approvedAt) {
}
