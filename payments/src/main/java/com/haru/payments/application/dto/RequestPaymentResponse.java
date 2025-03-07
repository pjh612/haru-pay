package com.haru.payments.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RequestPaymentResponse(UUID requestId,
                                     String orderId,
                                     UUID requestMemberId,
                                     String productName,
                                     BigDecimal requestPrice,
                                     UUID clientId,
                                     int paymentStatus,
                                     Instant approvedAt) {
}
