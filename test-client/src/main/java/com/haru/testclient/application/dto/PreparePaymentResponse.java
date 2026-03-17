package com.haru.testclient.application.dto;

import java.util.UUID;

public record PreparePaymentResponse(
        UUID paymentId
) {
}
