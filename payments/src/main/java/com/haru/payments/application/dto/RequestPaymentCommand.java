package com.haru.payments.application.dto;

import java.util.UUID;

public record RequestPaymentCommand(
        UUID paymentRequestId,
        UUID requestMemberId
) {
}
