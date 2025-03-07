package com.haru.payments.application.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ConfirmPaymentRequest(
        @NotNull
        UUID paymentId
) {
}
