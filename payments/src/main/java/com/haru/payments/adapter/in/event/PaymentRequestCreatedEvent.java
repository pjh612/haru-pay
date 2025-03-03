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
    private UUID sagaId;
    private UUID requestId;
    private UUID sellerId;
    private UUID requestMemberId;
    private Instant timestamp;
    private BigDecimal requestPrice;
    private String type;


    public PaymentRequestCreatedEvent(UUID sagaId, UUID requestId, UUID sellerId, UUID requestMemberId, BigDecimal requestPrice, String type) {
        this.sagaId = sagaId;
        this.requestId = requestId;
        this.sellerId = sellerId;
        this.requestMemberId = requestMemberId;
        this.requestPrice = requestPrice;
        this.timestamp = Instant.now();
        this.type = type;

    }

    @Override
    public UUID aggregateId() {
        return this.sagaId;
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

    public static PaymentRequestCreatedEvent success(UUID sagaId, UUID requestId, UUID sellerId, UUID requestMemberId, BigDecimal requestPrice) {
        return new PaymentRequestCreatedEvent(sagaId, requestId, sellerId, requestMemberId, requestPrice, "SUCCEEDED");
    }

    public static PaymentRequestCreatedEvent fail(UUID sagaId, UUID requestId, UUID sellerId, UUID requestMemberId, BigDecimal requestPrice) {
        return new PaymentRequestCreatedEvent(sagaId, requestId, sellerId, requestMemberId, requestPrice, "FAILED");
    }
}
