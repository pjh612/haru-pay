package com.haru.payments.common.alert;

import com.haru.payments.common.alert.cache.AlertCacheManager;
import com.haru.payments.common.alert.publisher.MessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
public class CommonAlertManager implements AlertManager {
    private final MessagePublisher messagePublisher;
    private final EmitterRepository emitterRepository;
    private final AlertCacheManager<MessageDto> alertCacheManager;

    public CommonAlertManager(MessagePublisher messagePublisher, EmitterRepository emitterRepository, AlertCacheManager<MessageDto> alertCacheManager) {
        this.messagePublisher = messagePublisher;
        this.emitterRepository = emitterRepository;
        this.alertCacheManager = alertCacheManager;
    }

    @Override
    public void notice(AlertChannel alertChannel, String targetId, Object message) {
        messagePublisher.publish(alertChannel.name(), MessageDto.of(targetId, message));
    }

    @Override
    public SseEmitter subscribe(AlertChannel alertChannel, String subscriberId, String lastEventId, Long timeoutMillis) {
        SseEmitter emitter = new SseEmitter(timeoutMillis);
        emitter.onTimeout(() -> {
            emitterRepository.deleteById(subscriberId);
            emitter.complete();
        });
        emitter.onCompletion(() -> emitterRepository.deleteById(subscriberId));
        emitter.onError(e -> {
            if(e != null) {
                log.error("Error occurred on SSE emitter id = {}, message = {}", subscriberId, e.getMessage(), e);
            }
            emitter.complete();
        });
        emitterRepository.put(subscriberId, emitter);
        messagePublisher.publish(alertChannel.name(), MessageDto.of(subscriberId, "connect", "connected"));
        if (StringUtils.hasText(lastEventId)) {
            String key = "alert:" + subscriberId;
            long offset = Long.parseLong(lastEventId);
            alertCacheManager.getFromOffset(key, offset, MessageDto.class)
                    .forEach(it -> messagePublisher.publish(alertChannel.name(), it));
        }

        return emitter;
    }
}
