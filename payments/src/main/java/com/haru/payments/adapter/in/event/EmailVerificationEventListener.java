package com.haru.payments.adapter.in.event;

import com.haru.payments.adapter.out.event.EmailVerificationEvent;
import com.haru.payments.application.usecase.SendVerificationEmailUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailVerificationEventListener {

    private final SendVerificationEmailUseCase sendVerificationEmailUseCase;

    @KafkaListener(
            topics = "email-verification",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handle(@Payload EmailVerificationEvent event) {
        log.info("EmailVerification event received for client: {}", event.clientId());

        try {
            sendVerificationEmailUseCase.send(
                    event.clientId(),
                    event.email(),
                    event.clientName(),
                    event.verificationToken()
            );
            log.info("Email sent successfully to: {}", event.email());
        } catch (Exception e) {
            log.error("Failed to process email verification event for client: {}", event.clientId(), e);
            throw e;
        }
    }
}
