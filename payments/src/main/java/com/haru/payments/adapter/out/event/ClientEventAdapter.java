package com.haru.payments.adapter.out.event;

import com.haru.payments.application.port.out.event.ClientEventPort;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClientEventAdapter implements ClientEventPort {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void sendEmailVerificationRequested(UUID clientId, String email, String clientName, String verificationToken) {
        EmailVerificationEvent event = new EmailVerificationEvent(clientId, email, verificationToken, clientName);
        kafkaTemplate.send("email-verification", event);
    }
}
