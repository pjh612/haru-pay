package com.haru.payments.common.alert.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper  objectMapper;

    @KafkaListener(groupId = "PAY_ALERT", topics = {"PAYMENT_RESULT"}, containerFactory = "alertKafkaListenerContainerFactory")
    public void consumeOrderEvent(ConsumerRecord<String, Object> record, Acknowledgment acknowledgment) {
        try {
            log.info("consume topic : {}, value : {}", record.topic(), record.value());
            redisTemplate.convertAndSend(record.topic(), objectMapper.writeValueAsString(record.value()));
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage());
        }
    }
}
