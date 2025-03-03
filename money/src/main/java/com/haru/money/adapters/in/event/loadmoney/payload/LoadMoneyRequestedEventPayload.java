package com.haru.money.adapters.in.event.loadmoney.payload;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoadMoneyRequestedEventPayload {
    private UUID requestId;
    private UUID memberId;
    private BigDecimal amount;
    private String type;
}
