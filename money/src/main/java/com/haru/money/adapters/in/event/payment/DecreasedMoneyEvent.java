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
    private UUID requestId;
    private UUID moneyChangingRequestId;
    private UUID requestMemberId;
    private BigDecimal requestPrice;
    private BigDecimal balance;
    private String failureReason;
    private String type;
    private Instant timestamp;

    public DecreasedMoneyEvent(UUID requestId, UUID moneyChangingRequestId, UUID requestMemberId, BigDecimal requestPrice, BigDecimal balance, String type, String failureReason) {
        this.requestId = requestId;
        this.moneyChangingRequestId = moneyChangingRequestId;
        this.requestMemberId = requestMemberId;
        this.requestPrice = requestPrice;
        this.balance = balance;
        this.type = type;
        this.timestamp = Instant.now();
        this.failureReason = failureReason;
    }

    @Override
    public UUID aggregateId() {
        return this.requestId;
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

    public static DecreasedMoneyEvent fail(UUID requestId, UUID moneyChangingRequestId, UUID requestMemberId, BigDecimal requestPrice, BigDecimal balance, String failureReason) {
        return new DecreasedMoneyEvent(
                requestId,
                moneyChangingRequestId,
                requestMemberId,
                requestPrice,
                balance,
                "FAILED",
                failureReason);
    }

    public static DecreasedMoneyEvent success(UUID requestId, UUID moneyChangingRequestId, UUID requestMemberId, BigDecimal requestPrice, BigDecimal balance) {
        return new DecreasedMoneyEvent(
                requestId,
                moneyChangingRequestId,
                requestMemberId,
                requestPrice,
                balance,
                "SUCCEEDED",
                null);
    }
}
