package com.haru.payments.application.dto;

import java.util.UUID;

public record RequestPaymentRequest(
        UUID paymentRequestId
) {
}
