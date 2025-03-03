package com.haru.payments.adapter.in.event;

import com.haru.payments.adapter.in.event.handler.ConfirmPaymentRequestEventHandler;
import com.haru.payments.adapter.in.event.payload.ConfirmPaymentRequestEventPayload;
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
public class ConfirmPaymentRequestEventListener {
    private final ConfirmPaymentRequestEventHandler handler;

    @KafkaListener(
            topics = "${kafka.topic.confirm-payment.inbox.events.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handle(
            @Header(KafkaHeaders.RECEIVED_KEY) UUID sagaId,
            @Header("id") String eventId,
            @Header("eventType") String eventType,
            @Payload ConfirmPaymentRequestEventPayload payload) {
        log.info("confirmPayment event received : {}", payload);
        handler.handle(sagaId, payload);
    }
}
