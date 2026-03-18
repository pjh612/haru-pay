package com.haru.payments.adapter.in.event;

import com.haru.common.util.UuidUtil;
import com.haru.payments.adapter.out.event.EmailVerificationEvent;
import com.haru.payments.application.port.out.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailVerificationEventListener {

    private final EmailService emailService;

    @KafkaListener(
            topics = "email-verification",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handle(@Payload EmailVerificationEvent event) {
        log.info("EmailVerification event received for client: {}", event.clientId());
        
        try {
            emailService.sendVerificationEmail(
                    event.email(),
                    event.clientName(),
                    event.verificationToken(),
                    event.clientId().toString()
            );
            log.info("Email sent successfully to: {}", event.email());
        } catch (Exception e) {
            log.error("Failed to process email verification event for client: {}", event.clientId(), e);
            throw e;
        }
    }
}
