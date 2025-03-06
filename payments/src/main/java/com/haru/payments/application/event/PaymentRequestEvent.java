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
public class PaymentRequestEvent implements OutboxEvent<UUID, PaymentRequestEvent> {
    private UUID requestId;
    private String orderId;
    private UUID clientId;
    private UUID requestMemberId;
    private BigDecimal requestPrice;
    private Instant timestamp;

    public PaymentRequestEvent(UUID requestId, String orderId, UUID clientId, UUID requestMemberId, BigDecimal requestPrice) {
        this.requestId = requestId;
        this.orderId = orderId;
        this.clientId = clientId;
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
        return "PaymentRequestEvent";
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
    public PaymentRequestEvent payload() {
        return this;
    }
}
