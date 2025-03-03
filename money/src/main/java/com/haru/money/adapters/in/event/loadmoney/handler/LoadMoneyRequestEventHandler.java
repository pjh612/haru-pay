package com.haru.money.adapters.in.event.loadmoney.handler;

import com.haru.money.adapters.in.event.loadmoney.payload.LoadMoneyRequestedEventPayload;
import com.haru.money.application.usecase.LoadMoneyUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LoadMoneyRequestEventHandler {
    private final LoadMoneyUseCase loadMoneyUseCase;

    @Transactional
    public void handle(UUID sagaId, LoadMoneyRequestedEventPayload payload) {
        if ("CANCEL".equals(payload.getType())) {
            loadMoneyUseCase.failRequestLoadMoney(sagaId);
        } else {
            loadMoneyUseCase.requestLoadMoney(payload.getRequestId(),
                    payload.getMemberId(),
                    payload.getAmount());
        }
    }
}
