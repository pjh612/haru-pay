package com.haru.payments.application.usecase.impl;

import com.haru.payments.application.usecase.SubscribePaymentResultUseCase;
import com.haru.payments.common.alert.AlertChannel;
import com.haru.payments.common.alert.AlertManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class SubscribePaymentResultService implements SubscribePaymentResultUseCase {
    private final AlertManager alarmManager;

    @Override
    public SseEmitter subscribe(String paymentId, String lastEventId) {
        return alarmManager.subscribe(AlertChannel.PAYMENT_RESULT, paymentId, lastEventId, Duration.ofSeconds(10).toMillis());
    }
}
