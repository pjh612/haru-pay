package com.haru.banking.adapter.in.event.loadmoney.listener;

import com.haru.banking.adapter.in.event.loadmoney.handler.RequestFirmBankingFinishedEventHandler;
import com.haru.banking.adapter.in.event.loadmoney.payload.RequestFirmBankingEventPayload;
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
public class RequestFirmBankingEventListener {

    private final Logger logger = LoggerFactory.getLogger(RequestFirmBankingEventListener.class);
    private final RequestFirmBankingFinishedEventHandler handler;

    @KafkaListener(
            topics = "${kafka.topic.firm-banking.inbox.events.name}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )

    public void handle(@Header(KafkaHeaders.RECEIVED_KEY) UUID sagaId,
                       @Header("id") String eventId,
                       @Header("eventType") String eventType,
                       @Payload RequestFirmBankingEventPayload payload) {
        logger.debug("Kafka message with key = {}, eventType {} arrived", sagaId, eventType);

        handler.handle(sagaId, payload);
    }
}
