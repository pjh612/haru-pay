package com.haru.money.adapters.in.event.payment;

import com.haru.money.adapters.in.event.payment.handler.DecreaseMoneyEventHandler;
import com.haru.money.adapters.in.event.payment.payload.DecreaseMoneyEventPayload;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DecreaseMoneyEventListener {
    private final Logger logger = LoggerFactory.getLogger(DecreaseMoneyEventListener.class);
    private final DecreaseMoneyEventHandler handler;

    @KafkaListener(
            topics = "${kafka.topic.decrease-money.inbox.events.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handle(@Header(KafkaHeaders.RECEIVED_KEY) UUID sagaId,
                       @Header("id") String eventId,
                       @Header("eventType") String eventType,
                       @Payload DecreaseMoneyEventPayload payload) {
        logger.debug("Kafka message received: key = {}, eventType = {}", sagaId, eventType);

        handler.handle(sagaId, payload);
    }
}
