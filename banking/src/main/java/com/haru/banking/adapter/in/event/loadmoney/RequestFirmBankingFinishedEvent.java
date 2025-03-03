package com.haru.banking.adapter.in.event.loadmoney;

import com.haru.common.event.OutboxEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestFirmBankingFinishedEvent implements OutboxEvent<String, RequestFirmBankingFinishedEvent> {
    private UUID sagaId;
    private UUID id;
    private UUID loadMoneyRequestId;
    private UUID memberId;
    private String fromBankName;
    private String fromBankAccountNumber;
    private String toBankName;
    private String toBankAccountNumber;
    private BigDecimal amount;
    private String type;
    private Instant timestamp;

    public RequestFirmBankingFinishedEvent(UUID sagaId, UUID id, UUID loadMoneyRequestId, UUID memberId, String fromBankName, String fromBankAccountNumber, String toBankName, String toBankAccountNumber, BigDecimal amount, String type) {
        this.sagaId = sagaId;
        this.id = id;
        this.loadMoneyRequestId = loadMoneyRequestId;
        this.memberId = memberId;
        this.fromBankName = fromBankName;
        this.fromBankAccountNumber = fromBankAccountNumber;
        this.toBankName = toBankName;
        this.toBankAccountNumber = toBankAccountNumber;
        this.amount = amount;
        this.type = type;
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
        return this.type;
    }

    @Override
    public Instant timestamp() {
        return this.timestamp;
    }

    @Override
    public RequestFirmBankingFinishedEvent payload() {
        return this;
    }
}
