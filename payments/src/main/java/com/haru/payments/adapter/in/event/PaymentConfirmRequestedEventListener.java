package com.haru.payments.adapter.in.event;

import com.haru.payments.adapter.in.event.handler.PaymentConfirmRequestedEventHandler;
import com.haru.payments.adapter.in.event.payload.PaymentConfirmRequestedEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConfirmRequestedEventListener {
    private final PaymentConfirmRequestedEventHandler handler;

    @KafkaListener(
            topics = "${kafka.topic.payment-confirm-request.inbox.events.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handle(
            @Header(KafkaHeaders.RECEIVED_KEY) UUID sagaId,
            @Header("id") String eventId,
            @Header("eventType") String eventType,
            @Payload PaymentConfirmRequestedEventPayload payload) {
        log.info("paymentRequest event received");
        handler.handle(payload);
    }

}
