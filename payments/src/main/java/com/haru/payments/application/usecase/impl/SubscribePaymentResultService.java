package com.haru.payments.application.usecase.impl;

import com.alert.core.manager.SubscribableAlertManager;
import com.haru.payments.adapter.out.alert.CommonAlertChannel;
import com.haru.payments.application.usecase.SubscribePaymentResultUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class SubscribePaymentResultService implements SubscribePaymentResultUseCase {
    private final SubscribableAlertManager<SseEmitter> alarmManager;

    @Override
    public SseEmitter subscribe(String paymentId, String lastEventId) {
        return alarmManager.subscribe(CommonAlertChannel.PAYMENT_RESULT, paymentId, lastEventId, Duration.ofSeconds(10).toMillis());
    }
}
