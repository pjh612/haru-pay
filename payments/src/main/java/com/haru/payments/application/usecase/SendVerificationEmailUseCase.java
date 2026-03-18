package com.haru.payments.application.usecase;

import java.util.UUID;

public interface SendVerificationEmailUseCase {
    void send(UUID clientId, String email, String clientName, String verificationToken);
}
