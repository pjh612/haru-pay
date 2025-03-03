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
    private Instant timestamp;

    public PaymentConfirmedEvent(UUID sagaId, UUID requestId, String type) {
        this.sagaId = sagaId;
        this.requestId = requestId;
        this.type = type;
        this.timestamp = Instant.now();
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
        return new PaymentConfirmedEvent(sagaId, requestId, "SUCCEEDED");
    }

    public static PaymentConfirmedEvent fail(UUID sagaId, UUID requestId) {
        return new PaymentConfirmedEvent(sagaId, requestId, "FAILED");
    }
}
