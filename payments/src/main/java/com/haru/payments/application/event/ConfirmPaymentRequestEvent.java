package com.haru.payments.application.event;

import com.haru.common.event.OutboxEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConfirmPaymentRequestEvent implements OutboxEvent<UUID, ConfirmPaymentRequestEvent> {
    private UUID requestId;
    private UUID requestMemberId;
    private BigDecimal requestPrice;
    private Instant timestamp;

    public ConfirmPaymentRequestEvent(UUID requestId, UUID requestMemberId, BigDecimal requestPrice) {
        this.requestId = requestId;
        this.requestMemberId = requestMemberId;
        this.requestPrice = requestPrice;
        this.timestamp = Instant.now();
    }

    @Override
    public UUID aggregateId() {
        return this.requestId;
    }

    @Override
    public String aggregateType() {
        return "ConfirmPaymentRequestEvent";
    }

    @Override
    public String type() {
        return "REQUEST";
    }

    @Override
    public Instant timestamp() {
        return this.timestamp;
    }

    @Override
    public ConfirmPaymentRequestEvent payload() {
        return this;
    }
}
