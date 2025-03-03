package com.haru.orchestrator.adapter.in.event.loadmoney.event.payload;

import com.haru.orchestrator.adapter.in.event.loadmoney.event.RequestFirmBankingEvent;
import com.haru.orchestrator.application.SagaPayload;
import com.haru.orchestrator.domain.model.PayloadType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckedRegisteredBankAccountEventPayload implements SagaPayload {
    private String loadMoneyRequestId;
    private String checkRegisteredBankAccountId;
    private String moneyId;
    private String memberId;
    private PayloadType type;
    private BigDecimal amount;
    private String fromBankName;
    private String fromBankAccountNumber;

    @Override
    public PayloadType type() {
        return type;
    }

    @Override
    public RequestFirmBankingEvent toEvent() {
        return new RequestFirmBankingEvent(
                this.loadMoneyRequestId,
                this.memberId,
                this.fromBankName,
                this.fromBankAccountNumber,
                "joy",
                "joy",
                this.amount);
    }
}
