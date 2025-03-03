package com.haru.money.adapters.in.event.payment;

import com.haru.common.event.OutboxEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DecreasedMoneyEvent implements OutboxEvent<UUID, DecreasedMoneyEvent> {
    private UUID sagaId;
    private UUID requestId;
    private UUID moneyChangingRequestId;
    private UUID requestMemberId;
    private BigDecimal requestPrice;
    private BigDecimal balance;
    private String type;
    private Instant timestamp;

    public DecreasedMoneyEvent(UUID sagaId, UUID requestId, UUID moneyChangingRequestId, UUID requestMemberId, BigDecimal requestPrice, BigDecimal balance, String type) {
        this.sagaId = sagaId;
        this.requestId = requestId;
        this.moneyChangingRequestId = moneyChangingRequestId;
        this.requestMemberId = requestMemberId;
        this.requestPrice = requestPrice;
        this.balance = balance;
        this.type = type;
        this.timestamp = Instant.now();
    }

    @Override
    public UUID aggregateId() {
        return this.sagaId;
    }

    @Override
    public String aggregateType() {
        return "DecreasedMoneyEvent";
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
    public DecreasedMoneyEvent payload() {
        return this;
    }

    public static DecreasedMoneyEvent fail(UUID sagaId, UUID requestId, UUID moneyChangingRequestId, UUID requestMemberId, BigDecimal requestPrice, BigDecimal balance) {
        return new DecreasedMoneyEvent(
                sagaId,
                requestId,
                moneyChangingRequestId,
                requestMemberId,
                requestPrice,
                balance,
                "FAILED");
    }

    public static DecreasedMoneyEvent success(UUID sagaId, UUID requestId, UUID moneyChangingRequestId, UUID requestMemberId, BigDecimal requestPrice, BigDecimal balance) {
        return new DecreasedMoneyEvent(
                sagaId,
                requestId,
                moneyChangingRequestId,
                requestMemberId,
                requestPrice,
                balance,
                "SUCCEEDED");
    }
}
