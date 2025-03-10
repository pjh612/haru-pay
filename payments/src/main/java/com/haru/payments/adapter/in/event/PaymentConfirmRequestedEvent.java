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
public class PaymentConfirmRequestedEvent implements OutboxEvent<UUID, PaymentConfirmRequestedEvent> {
    private UUID requestId;
    private UUID requestMemberId;
    private BigDecimal requestPrice;
    private String failureReason;
    private String type;
    private Instant timestamp;


    public PaymentConfirmRequestedEvent(UUID requestId, UUID requestMemberId, BigDecimal requestPrice, String type, String failureReason) {
        this.requestId = requestId;
        this.requestMemberId = requestMemberId;
        this.requestPrice= requestPrice;
        this.type = type;
        this.timestamp = Instant.now();
        this.failureReason = failureReason;
    }

    @Override
    public UUID aggregateId() {
        return this.requestId;
    }

    @Override
    public String aggregateType() {
        return "PaymentConfirmRequestedEvent";
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
    public PaymentConfirmRequestedEvent payload() {
        return this;
    }

    public static PaymentConfirmRequestedEvent success(UUID requestId, UUID requestMemberId,BigDecimal requestPrice) {
        return new PaymentConfirmRequestedEvent(requestId, requestMemberId, requestPrice,"SUCCEEDED", null);
    }

    public static PaymentConfirmRequestedEvent fail(UUID requestId, UUID requestMemberId,BigDecimal requestPrice, String failureReason) {
        return new PaymentConfirmRequestedEvent(requestId, requestMemberId, requestPrice,"FAILED", failureReason);
    }
}
