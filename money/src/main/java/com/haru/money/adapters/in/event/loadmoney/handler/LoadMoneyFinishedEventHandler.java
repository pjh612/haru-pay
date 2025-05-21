package com.haru.money.adapters.in.event.loadmoney.handler;

import com.haru.common.RequiresNewExecutor;
import com.haru.money.adapters.in.event.loadmoney.LoadMoneyFinishedEvent;
import com.haru.money.adapters.in.event.loadmoney.payload.FirmBankingFinishedPayload;
import com.haru.money.application.usecase.LoadMoneyEdaUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoadMoneyFinishedEventHandler {
    private final LoadMoneyEdaUseCase loadMoneyUseCase;
    private final ApplicationEventPublisher eventPublisher;
    private final RequiresNewExecutor requiresNewExecutor;

    @Transactional
    public void handle(UUID sagaId, FirmBankingFinishedPayload payload) {
        try {
            loadMoneyUseCase.loadMoney(sagaId);
        } catch (Exception e) {
            requiresNewExecutor.execute(() -> {
                LoadMoneyFinishedEvent failEvent = LoadMoneyFinishedEvent.fail(payload.getLoadMoneyRequestId());
                eventPublisher.publishEvent(failEvent);
            });
        }
    }
}
