package com.haru.payments.common.alert.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haru.payments.common.alert.EmitterRepository;
import com.haru.payments.common.alert.MessageDto;
import com.haru.payments.common.alert.cache.AlertCacheManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Objects;

@Component
public class AlertMessageSubscriber implements MessageListener {
    private final EmitterRepository emitterRepository;
    private final AlertCacheManager<MessageDto> alertCacheManager;
    private final ObjectMapper objectMapper;

    private static final String MESSAGE_TYPE = "message";
    private static final String ALERT_CACHE_KEY_PREFIX = "alert:";

    public AlertMessageSubscriber(EmitterRepository emitterRepository, AlertCacheManager<MessageDto> alertCacheManager, ObjectMapper objectMapper) {
        this.emitterRepository = emitterRepository;
        this.alertCacheManager = alertCacheManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            long id = System.currentTimeMillis();
            MessageDto alertMessage = objectMapper.readValue(message.getBody(), MessageDto.class);

            emitterRepository.getById(alertMessage.targetId())
                    .ifPresent(it -> sendEvent(it, id, alertMessage));

            if (Objects.equals(alertMessage.type(), MESSAGE_TYPE)) {
                alertCacheManager.save(ALERT_CACHE_KEY_PREFIX + alertMessage.targetId(), Long.toString(id), alertMessage);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendEvent(SseEmitter emitter, long id, MessageDto messageDto) {
        SseEmitter.SseEventBuilder eventBuilder = SseEmitter.event()
                .name(messageDto.type())
                .data(messageDto.body())
                .id(Long.toString(id));
        try {
            emitter.send(eventBuilder.build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
