package com.haru.payments.application.dto;

import java.math.BigDecimal;

public record PreparePaymentRequest(
        String orderId,
        BigDecimal requestPrice,
        String productName
) {
}
