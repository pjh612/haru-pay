package com.haru.payments.adapter.in.event;

import com.haru.common.event.OutboxEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentRequestCreatedEvent implements OutboxEvent<UUID, PaymentRequestCreatedEvent> {
    private UUID requestId;
    private UUID clientId;
    private UUID requestMemberId;
    private Instant timestamp;
    private BigDecimal requestPrice;
    private String failureReason;
    private String type;


    public PaymentRequestCreatedEvent(UUID requestId, UUID clientId, UUID requestMemberId, BigDecimal requestPrice, String type, String failureReason) {
        this.requestId = requestId;
        this.clientId = clientId;
        this.requestMemberId = requestMemberId;
        this.requestPrice = requestPrice;
        this.timestamp = Instant.now();
        this.type = type;
        this.failureReason = failureReason;

    }

    @Override
    public UUID aggregateId() {
        return this.requestId;
    }

    @Override
    public String aggregateType() {
        return "PaymentRequestCreatedEvent";
    }

    @Override
    public String type() {
        return this.type;
    }

    @Override
    public Instant timestamp() {
        return this.timestamp;
    }

    @Override
    public PaymentRequestCreatedEvent payload() {
        return this;
    }

    public static PaymentRequestCreatedEvent success(UUID requestId, UUID sellerId, UUID requestMemberId, BigDecimal requestPrice) {
        return new PaymentRequestCreatedEvent(requestId, sellerId, requestMemberId, requestPrice, "SUCCEEDED",null);
    }

    public static PaymentRequestCreatedEvent fail(UUID requestId, UUID sellerId, UUID requestMemberId, BigDecimal requestPrice, String failureReason) {
        return new PaymentRequestCreatedEvent(requestId, sellerId, requestMemberId, requestPrice, "FAILED", failureReason);
    }
}
