package com.haru.orchestrator.adapter.in.event.payment.event.payload;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DecreaseMoneyEvent {
    private UUID requestId;
    private UUID requestMemberId;
    private BigDecimal requestPrice;
    private String failureReason;
}
