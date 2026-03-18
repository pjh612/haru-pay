package com.haru.payments.adapter.out.event;

import java.util.UUID;

public record EmailVerificationEvent(
        UUID clientId,
        String email,
        String verificationToken,
        String clientName
) {
}
