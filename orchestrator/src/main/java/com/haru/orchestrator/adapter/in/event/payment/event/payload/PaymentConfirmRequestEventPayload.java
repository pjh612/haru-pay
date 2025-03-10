package com.haru.orchestrator.adapter.in.event.payment.event.payload;

import com.haru.orchestrator.domain.model.PayloadType;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentConfirmRequestEventPayload(
        UUID requestId,
        UUID requestMemberId,
        BigDecimal requestPrice,
        String failureReason,
        PayloadType type) {
}
