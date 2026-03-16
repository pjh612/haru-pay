package com.haru.testclient.application.dto;

import java.math.BigDecimal;

public record PreparePaymentRequest(
        String orderId,
        String productName,
        BigDecimal requestPrice
) {
}
