package com.haru.orchestrator.adapter.in.event.loadmoney.event.payload;

import com.haru.orchestrator.adapter.in.event.loadmoney.event.RequestLoadMoneyFinishedEvent;
import com.haru.orchestrator.application.SagaPayload;
import com.haru.orchestrator.domain.model.PayloadType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RequestFirmBankingFinishedEventPayload implements SagaPayload {
    private String id;
    private String loadMoneyRequestId;
    private String memberId;
    private String fromBankName;
    private String fromBankAccountNumber;
    private String toBankName;
    private String toBankAccountNumber;
    private BigDecimal amount;
    private PayloadType type;

    @Override
    public PayloadType type() {
        return type;
    }

    @Override
    public RequestLoadMoneyFinishedEvent toEvent() {
        return new RequestLoadMoneyFinishedEvent(this.loadMoneyRequestId, this.memberId, this.amount);
    }

    @Override
    public String failureReason() {
        return "";
    }
}
