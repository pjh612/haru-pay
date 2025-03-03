package com.haru.payments.adapter.in.event;

import com.haru.payments.adapter.in.event.handler.PaymentRequestEventHandler;
import com.haru.payments.adapter.in.event.payload.CreatePaymentRequestEventPayload;
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
public class PaymentRequestEventListener {
    private final PaymentRequestEventHandler handler;

    @KafkaListener(
            topics = "${kafka.topic.payment-request.inbox.events.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handle(
            @Header(KafkaHeaders.RECEIVED_KEY) UUID sagaId,
            @Header("id") String eventId,
            @Header("eventType") String eventType,
            @Payload CreatePaymentRequestEventPayload payload) {
        log.info("paymentRequest event received");
        handler.handle(sagaId, payload);
    }

}
