package com.haru.banking.adapter.in.event.loadmoney;

import com.haru.common.event.OutboxEvent;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CheckedRegisteredBankAccountEvent implements OutboxEvent<String, CheckedRegisteredBankAccountEvent> {
    private UUID sagaId;
    private UUID loadMoneyRequestId;
    private UUID checkRegisteredBankAccountId;
    private UUID moneyId;
    private UUID memberId;
    private String type;
    private BigDecimal amount;
    private String fromBankName;
    private String fromBankAccountNumber;
    private Instant timestamp;

    public CheckedRegisteredBankAccountEvent(UUID sagaId, UUID loadMoneyRequestId, UUID checkRegisteredBankAccountId, UUID moneyId, UUID memberId, String type, BigDecimal amount, String fromBankName, String fromBankAccountNumber) {
        this.sagaId = sagaId;
        this.loadMoneyRequestId = loadMoneyRequestId;
        this.checkRegisteredBankAccountId = checkRegisteredBankAccountId;
        this.moneyId = moneyId;
        this.memberId = memberId;
        this.type = type;
        this.amount = amount;
        this.fromBankName = fromBankName;
        this.fromBankAccountNumber = fromBankAccountNumber;
        this.timestamp = Instant.now();
    }

    @Override
    public String aggregateId() {
        return this.sagaId.toString();
    }

    @Override
    public String aggregateType() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public Instant timestamp() {
        return this.timestamp;
    }

    @Override
    public CheckedRegisteredBankAccountEvent payload() {
        return this;
    }
}
