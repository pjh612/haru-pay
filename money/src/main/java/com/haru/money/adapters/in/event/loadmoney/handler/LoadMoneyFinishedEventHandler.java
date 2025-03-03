package com.haru.money.adapters.in.event.loadmoney.handler;

import com.haru.money.adapters.in.event.loadmoney.payload.FirmBankingFinishedPayload;
import com.haru.money.application.usecase.LoadMoneyUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoadMoneyFinishedEventHandler {
    private final LoadMoneyUseCase loadMoneyUseCase;

    @Transactional
    public void handle(UUID sagaId, FirmBankingFinishedPayload payload) {
        loadMoneyUseCase.loadMoney(sagaId);
    }
}
