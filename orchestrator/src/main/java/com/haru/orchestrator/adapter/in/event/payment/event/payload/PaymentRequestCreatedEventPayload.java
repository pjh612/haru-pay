package com.haru.orchestrator.adapter.in.event.payment.event.payload;

import com.haru.orchestrator.application.SagaPayload;
import com.haru.orchestrator.domain.model.PayloadType;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentRequestCreatedEventPayload implements SagaPayload {
    private UUID requestId;
    private UUID sellerId;
    private UUID requestMemberId;
    private BigDecimal requestPrice;
    private PayloadType type;

    @Override
    public PayloadType type() {
        return this.type;
    }

    @Override
    public Object toEvent() {
        return new DecreaseMoneyEvent(
                this.requestId,
                this.sellerId,
                this.requestMemberId,
                this.requestPrice
        );
    }
}
