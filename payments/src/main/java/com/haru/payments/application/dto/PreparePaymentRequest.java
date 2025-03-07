package com.haru.payments.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PreparePaymentRequest(
        @NotNull
        String orderId,
        @NotNull
        BigDecimal requestPrice,
        @NotBlank
        String productName
) {
}
