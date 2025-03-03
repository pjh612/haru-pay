package com.haru.money.adapters.in.event.loadmoney;

import com.haru.common.event.OutboxEvent;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoadMoneyRequestCreatedEvent implements OutboxEvent<String, LoadMoneyRequestCreatedEvent> {
    private UUID requestId;
    private UUID moneyId;
    private UUID memberId;
    private BigDecimal amount;
    private String type;
    private Instant timestamp;

    public LoadMoneyRequestCreatedEvent(UUID requestId, UUID moneyId, UUID memberId, BigDecimal amount, String type) {
        this.requestId = requestId;
        this.moneyId = moneyId;
        this.memberId = memberId;
        this.amount = amount;
        this.type = type;
        this.timestamp = Instant.now();
    }

    @Override
    public String aggregateId() {
        return this.requestId.toString();
    }

    @Override
    public String aggregateType() {
        return "LoadMoneyRequestCreatedEvent";
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
    public LoadMoneyRequestCreatedEvent payload() {
        return this;
    }

    public static LoadMoneyRequestCreatedEvent success(UUID requestId, UUID moneyId, UUID memberId, BigDecimal amount) {
        return new LoadMoneyRequestCreatedEvent(requestId,
                moneyId,
                memberId,
                amount,
                "SUCCEEDED");
    }

    public static LoadMoneyRequestCreatedEvent fail(UUID requestId) {
        return new LoadMoneyRequestCreatedEvent(
                requestId,
                null,
                null,
                null,
                "FAILED");
    }
}
