package com.haru.payments.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PreparePaymentCommand(
        UUID clientId,
        BigDecimal requestPrice,
        String productName
) {
}
