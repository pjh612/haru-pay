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
public class CreatePaymentRequestEventPayload {
    private UUID requestId;
    private String orderId;
    private UUID requestMemberId;
    private BigDecimal requestPrice;
    private UUID clientId;
    private String type;
}
