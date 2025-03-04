package com.haru.payments.application.dto;

import java.math.BigDecimal;

public record PreparePaymentRequest(
        BigDecimal requestPrice,
        String productName
) {
}
