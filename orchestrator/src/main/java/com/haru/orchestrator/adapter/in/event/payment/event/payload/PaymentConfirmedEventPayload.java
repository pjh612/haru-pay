package com.haru.orchestrator.adapter.in.event.payment.event.payload;

import com.haru.orchestrator.application.SagaPayload;
import com.haru.orchestrator.domain.model.PayloadType;
import lombok.*;

import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentConfirmedEventPayload implements SagaPayload {
    private UUID sagaId;
    private UUID requestId;
    private String failureReason;
    private PayloadType type;

    @Override
    public PayloadType type() {
        return this.type;
    }

    @Override
    public Object toEvent() {
        return null;
    }

    @Override
    public String failureReason() {
        return this.failureReason;
    }
}
