package com.haru.payments.application.usecase.impl;

import com.alert.core.manager.SubscribableAlertManager;
import com.haru.payments.adapter.out.alert.CommonAlertChannel;
import com.haru.payments.application.usecase.SubscribePaymentResultUseCase;
import com.haru.payments.domain.model.PaymentRequest;
import com.haru.payments.domain.repository.PaymentRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscribePaymentResultService implements SubscribePaymentResultUseCase {
    private final SubscribableAlertManager<SseEmitter> alarmManager;
    private final PaymentRequestRepository paymentRequestRepository;

    @Override
    public SseEmitter subscribe(UUID paymentId, UUID clientId, String lastEventId) {
        PaymentRequest paymentRequest = paymentRequestRepository.findByIdAndClientId(paymentId, clientId)
                .orElseThrow(() -> new EntityNotFoundException("결제 정보를 찾을 수 없습니다."));

        return alarmManager.subscribe(CommonAlertChannel.PAYMENT_RESULT, paymentRequest.getRequestId().toString(), null, lastEventId, Duration.ofMinutes(10).toMillis());
    }
}
