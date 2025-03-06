package com.haru.orchestrator.adapter.in.event.payment.event.payload;

import com.haru.orchestrator.domain.model.PayloadType;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequestEventPayload(
        UUID requestId,
        UUID requestMemberId,
        BigDecimal requestPrice,
        UUID clientId,
        PayloadType type) {
}
