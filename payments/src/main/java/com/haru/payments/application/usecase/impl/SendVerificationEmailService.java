package com.haru.payments.application.usecase.impl;

import com.haru.payments.application.port.out.mail.EmailNotificationPort;
import com.haru.payments.application.usecase.SendVerificationEmailUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SendVerificationEmailService implements SendVerificationEmailUseCase {
    private final EmailNotificationPort emailService;

    @Override
    public void send(UUID clientId, String email, String clientName, String verificationToken) {
        emailService.sendVerificationEmail(email, clientName, verificationToken, clientId.toString());
    }
}
