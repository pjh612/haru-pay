package com.haru.money.adapters.in.event.payment.handler;

import com.haru.common.RequiresNewExecutor;
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
    private final RequiresNewExecutor requiresNewExecutor;

    @Transactional
    public void handle(DecreaseMoneyEventPayload payload) {
        if ("CANCEL".equals(payload.getType())) {
            decreaseMoneyUseCase.cancelDecreaseMoneyRequest(payload.getRequestId(), payload.getRequestMemberId(), payload.getRequestPrice());
            publishFailEvent(payload, payload.getFailureReason());
        } else {
            try {
                DecreaseMoneyResponse response = decreaseMoneyUseCase.decrease(payload.getRequestId(), payload.getRequestMemberId(), payload.getRequestPrice());
                publishSuccessEvent(payload, response.balance());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                requiresNewExecutor.execute(() -> publishFailEvent(payload, e.getMessage()));
            }
        }
    }

    private void publishSuccessEvent(DecreaseMoneyEventPayload payload, BigDecimal balance) {
        DecreasedMoneyEvent event = DecreasedMoneyEvent.success(
                payload.getRequestId(),
                payload.getRequestId(),
                payload.getRequestMemberId(),
                payload.getRequestPrice(),
                balance);
        eventPublisher.publishEvent(event);
    }

    private void publishFailEvent(DecreaseMoneyEventPayload payload, String failureReason) {
        DecreasedMoneyEvent failedEvent = DecreasedMoneyEvent.fail(
                payload.getRequestId(),
                payload.getRequestId(),
                payload.getRequestMemberId(),
                payload.getRequestPrice(),
                null,
                failureReason
        );
        eventPublisher.publishEvent(failedEvent);
    }
}
