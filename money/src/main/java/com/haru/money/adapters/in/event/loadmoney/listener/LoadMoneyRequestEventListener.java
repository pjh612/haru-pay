package com.haru.money.adapters.in.event.loadmoney.listener;

import com.haru.money.adapters.in.event.loadmoney.handler.LoadMoneyRequestEventHandler;
import com.haru.money.adapters.in.event.loadmoney.payload.LoadMoneyRequestedEventPayload;
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
public class LoadMoneyRequestEventListener {
    private final Logger logger = LoggerFactory.getLogger(LoadMoneyRequestEventListener.class);
    private final LoadMoneyRequestEventHandler handler;

    @KafkaListener(
            topics = "${kafka.topic.load-money-request.inbox.events.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handle(@Header(KafkaHeaders.RECEIVED_KEY) UUID sagaId,
                       @Header("id") String eventId,
                       @Header("eventType") String eventType,
                       @Payload LoadMoneyRequestedEventPayload payload) {
        logger.debug("Kafka message received: key = {}, eventType = {}", sagaId, eventType);

        handler.handle(sagaId, payload);
    }
}
