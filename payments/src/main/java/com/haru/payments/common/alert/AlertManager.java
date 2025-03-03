package com.haru.payments.common.alert;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AlertManager {

    void notice(AlertChannel alertChannel, String targetId, Object message);

    SseEmitter subscribe(AlertChannel alertChannel, String subscriberId, String lastEventId, Long timeoutMillis);
}
