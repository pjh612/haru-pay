package com.haru.payments.application.port.out.mail;

public interface EmailNotificationPort {
    void sendVerificationEmail(String to, String clientName, String verificationToken, String clientId);
}
