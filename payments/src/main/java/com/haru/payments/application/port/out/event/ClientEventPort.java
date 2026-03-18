package com.haru.payments.application.port.out.event;

import java.util.UUID;

public interface ClientEventPort {
    void sendEmailVerificationRequested(UUID clientId, String email, String clientName, String verificationToken);
}
