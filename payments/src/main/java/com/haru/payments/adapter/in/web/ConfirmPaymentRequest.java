package com.haru.payments.adapter.in.web;

import java.util.UUID;

public record ConfirmPaymentRequest(
        UUID paymentId
) {
}
