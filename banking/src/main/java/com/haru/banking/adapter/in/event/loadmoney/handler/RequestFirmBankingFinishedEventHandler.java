package com.haru.banking.adapter.in.event.loadmoney.handler;

import com.haru.banking.adapter.in.event.loadmoney.RequestFirmBankingFinishedEvent;
import com.haru.banking.adapter.in.event.loadmoney.payload.RequestFirmBankingEventPayload;
import com.haru.banking.appliation.dto.RequestFirmBankingRequest;
import com.haru.banking.appliation.dto.RequestFirmBankingResponse;
import com.haru.banking.appliation.usecase.RequestFirmBankingUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RequestFirmBankingFinishedEventHandler {
    private final RequestFirmBankingUseCase requestFirmBankingUseCase;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void handle(UUID sagaId, RequestFirmBankingEventPayload payload) {
        if ("CANCEL".equals(payload.getType())) {
            eventPublisher.publishEvent(new RequestFirmBankingFinishedEvent(
                    sagaId,
                    null,
                    payload.getLoadMoneyRequestId(),
                    payload.getMemberId(),
                    payload.getFromBankName(),
                    payload.getFromBankAccountNumber(),
                    payload.getToBankName(),
                    payload.getToBankAccountNumber(),
                    payload.getAmount(),
                    "FAILED"
            ));
        } else {
            RequestFirmBankingRequest requestFirmBankingRequest = new RequestFirmBankingRequest(
                    payload.getFromBankName(),
                    payload.getFromBankAccountNumber(),
                    payload.getToBankName(),
                    payload.getToBankAccountNumber(),
                    payload.getAmount()
            );
            RequestFirmBankingResponse requestFirmBankingResponse = requestFirmBankingUseCase.request(requestFirmBankingRequest);

            eventPublisher.publishEvent(new RequestFirmBankingFinishedEvent(
                    sagaId,
                    requestFirmBankingResponse.id(),
                    payload.getLoadMoneyRequestId(),
                    payload.getMemberId(),
                    payload.getFromBankName(),
                    payload.getFromBankAccountNumber(),
                    payload.getToBankName(),
                    payload.getToBankAccountNumber(),
                    payload.getAmount(),
                    requestFirmBankingResponse.status()
            ));
        }
    }
}
