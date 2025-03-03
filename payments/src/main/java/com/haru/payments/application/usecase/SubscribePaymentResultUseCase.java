package com.haru.payments.application.usecase;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SubscribePaymentResultUseCase {
    SseEmitter subscribe(String paymentId, String lastEventId);
}
