package com.haru.orchestrator.adapter.in.event.loadmoney.event.payload;

import com.haru.orchestrator.adapter.in.event.loadmoney.event.CheckRegisteredBankAccountEvent;
import com.haru.orchestrator.application.SagaPayload;
import com.haru.orchestrator.domain.model.PayloadType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoadMoneyRequestCreatedEventPayload implements SagaPayload {
    private UUID requestId;
    private UUID moneyId;
    private String memberId;
    private BigDecimal amount;
    private PayloadType type;

    @Override
    public PayloadType type() {
        return this.type;
    }

    @Override
    public CheckRegisteredBankAccountEvent toEvent() {
        return new CheckRegisteredBankAccountEvent(
                this.requestId,
                this.memberId,
                this.moneyId,
                this.amount);
    }

    @Override
    public String failureReason() {
        return "";
    }
}
