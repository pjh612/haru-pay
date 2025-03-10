package com.haru.payments.adapter.in.event.payload;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentConfirmRequestedEventPayload {
    private UUID requestId;
    private UUID requestMemberId;
    private BigDecimal requestPrice;
    private String failureReason;
    private String type;
}
