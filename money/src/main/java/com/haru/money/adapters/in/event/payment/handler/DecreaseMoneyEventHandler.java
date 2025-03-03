package com.haru.money.adapters.in.event.payment.handler;

import com.haru.money.adapters.in.event.payment.DecreasedMoneyEvent;
import com.haru.money.adapters.in.event.payment.payload.DecreaseMoneyEventPayload;
import com.haru.money.application.dto.DecreaseMoneyResponse;
import com.haru.money.application.usecase.DecreaseMoneyUseCase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DecreaseMoneyEventHandler {
    private final DecreaseMoneyUseCase decreaseMoneyUseCase;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void handle(UUID sagaId, DecreaseMoneyEventPayload payload) {
        if ("CANCEL".equals(payload.getType())) {
            decreaseMoneyUseCase.onDecreaseFailed(payload.getRequestId(), payload.getRequestMemberId(), payload.getRequestPrice());
            publishFailEvent(sagaId, payload);
        } else {
            try {
                DecreaseMoneyResponse response = decreaseMoneyUseCase.decrease(payload.getRequestId(), payload.getRequestMemberId(), payload.getRequestPrice());
                publishSuccessEvent(sagaId, payload, response.balance());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                publishFailEvent(sagaId, payload);
            }
        }
    }

    private void publishSuccessEvent(UUID sagaId, DecreaseMoneyEventPayload payload, BigDecimal balance) {
        DecreasedMoneyEvent event = DecreasedMoneyEvent.success(
                sagaId,
                payload.getRequestId(),
                payload.getRequestId(),
                payload.getRequestMemberId(),
                payload.getRequestPrice(),
                balance);
        eventPublisher.publishEvent(event);
    }

    private void publishFailEvent(UUID sagaId, DecreaseMoneyEventPayload payload) {
        eventPublisher.publishEvent(DecreasedMoneyEvent.fail(
                sagaId,
                payload.getRequestId(),
                payload.getRequestId(),
                payload.getRequestMemberId(),
                payload.getRequestPrice(),
                null));
    }
}
