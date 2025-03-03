package com.haru.money.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MoneyChangingRequest {
    private UUID id;
    private UUID targetMemberId;
    private ChangingType changingType;
    private BigDecimal amount;
    private ChangingStatus status;
    private Instant createdAt;

    public static MoneyChangingRequest create(UUID targetMemberId,
                                              ChangingType changingType, BigDecimal amount) {
        return new MoneyChangingRequest(
                null,
                targetMemberId,
                changingType,
                amount,
                ChangingStatus.REQUESTED,
                Instant.now()
        );
    }

    public static MoneyChangingRequest createNew(UUID id, UUID targetMemberId,
                                                 ChangingType changingType, BigDecimal amount) {
        return new MoneyChangingRequest(
                id,
                targetMemberId,
                changingType,
                amount,
                ChangingStatus.REQUESTED,
                Instant.now()
        );
    }

    public MoneyChangingRequest setStatus(ChangingStatus status) {
        this.status = status;

        return this;
    }

    public void success() {
        this.status = ChangingStatus.SUCCEEDED;
    }

    public void fail() {
        this.status = ChangingStatus.FAILED;
    }
}
