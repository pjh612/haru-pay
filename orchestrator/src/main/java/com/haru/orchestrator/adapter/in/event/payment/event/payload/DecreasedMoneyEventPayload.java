package com.haru.orchestrator.adapter.in.event.payment.event.payload;

import com.haru.orchestrator.adapter.in.event.payment.event.ConfirmPaymentRequestEvent;
import com.haru.orchestrator.application.SagaPayload;
import com.haru.orchestrator.domain.model.PayloadType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DecreasedMoneyEventPayload implements SagaPayload {
    private UUID requestId;
    private UUID moneyChangingRequestId;
    private UUID requestMemberId;
    private BigDecimal requestPrice;
    private BigDecimal balance;
    private String failureReason;
    private PayloadType type;

    @Override
    public PayloadType type() {
        return this.type;
    }

    @Override
    public Object toEvent() {
        return new ConfirmPaymentRequestEvent(this.requestId, this.requestMemberId, this.requestPrice, this.balance, this.failureReason);
    }

    @Override
    public String failureReason() {
        return this.failureReason;
    }
}
