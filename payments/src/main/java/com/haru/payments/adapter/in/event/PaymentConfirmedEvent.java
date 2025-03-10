package com.haru.payments.adapter.in.event;

import com.haru.common.event.OutboxEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentConfirmedEvent implements OutboxEvent<UUID, PaymentConfirmedEvent> {
    private UUID sagaId;
    private UUID requestId;
    private String type;
    private String failureReason;
    private Instant timestamp;

    public PaymentConfirmedEvent(UUID sagaId, UUID requestId, String type,String failureReason) {
        this.sagaId = sagaId;
        this.requestId = requestId;
        this.type = type;
        this.timestamp = Instant.now();
        this.failureReason =failureReason;
    }

    @Override
    public UUID aggregateId() {
        return sagaId;
    }

    @Override
    public String aggregateType() {
        return "PaymentConfirmedEvent";
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
    public PaymentConfirmedEvent payload() {
        return this;
    }

    public static PaymentConfirmedEvent success(UUID sagaId, UUID requestId) {
        return new PaymentConfirmedEvent(sagaId, requestId, "SUCCEEDED", null);
    }

    public static PaymentConfirmedEvent fail(UUID sagaId, UUID requestId, String failureReason) {
        return new PaymentConfirmedEvent(sagaId, requestId, "FAILED", failureReason);
    }
}
