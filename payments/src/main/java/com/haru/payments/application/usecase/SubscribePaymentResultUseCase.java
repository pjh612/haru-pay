package com.haru.payments.application.usecase;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

public interface SubscribePaymentResultUseCase {
    SseEmitter subscribe(UUID paymentId, UUID clientId, String lastEventId);
}
