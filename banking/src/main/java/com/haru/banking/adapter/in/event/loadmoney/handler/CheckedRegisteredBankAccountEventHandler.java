package com.haru.banking.adapter.in.event.loadmoney.handler;

import com.haru.banking.adapter.in.event.loadmoney.CheckedRegisteredBankAccountEvent;
import com.haru.banking.adapter.in.event.loadmoney.payload.CheckRegisteredBankAccountEventPayload;
import com.haru.banking.appliation.client.BankAccountInfoClient;
import com.haru.banking.appliation.client.dto.BankAccount;
import com.haru.banking.domain.model.RegisteredBankAccount;
import com.haru.banking.domain.repository.RegisteredAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckedRegisteredBankAccountEventHandler {
    private final BankAccountInfoClient bankAccountInfoClient;
    private final RegisteredAccountRepository registeredAccountRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void handle(UUID sagaId, CheckRegisteredBankAccountEventPayload payload) {
        if ("CANCEL".equals(payload.getType())) {
            publishFailEvent(sagaId, payload);
        } else {
            processRegisteredBankAccount(sagaId, payload);
        }
    }

    private void processRegisteredBankAccount(UUID sagaId, CheckRegisteredBankAccountEventPayload payload) {
        registeredAccountRepository.findByMemberId(payload.getMemberId())
                .ifPresentOrElse(
                        registeredBankAccount -> processValidateBankAccount(sagaId, payload, registeredBankAccount),
                        () -> publishFailEvent(sagaId, payload)
                );
    }

    private void processValidateBankAccount(UUID sagaId, CheckRegisteredBankAccountEventPayload payloadObject, RegisteredBankAccount registeredBankAccount) {
        BankAccount bankAccountInfo = bankAccountInfoClient.getBankAccountInfo(
                registeredBankAccount.getBankName(),
                registeredBankAccount.getAccountNumber()
        );

        if (bankAccountInfo != null) {
            log.debug("Valid bank account found for memberId: {}", payloadObject.getMemberId());
            publishSuccessEvent(sagaId, payloadObject, registeredBankAccount);
        } else {
            log.debug("Bank account info not found for memberId: {}", payloadObject.getMemberId());
            publishFailEvent(sagaId, payloadObject);
        }
    }

    private void publishSuccessEvent(UUID sagaId, CheckRegisteredBankAccountEventPayload payloadObject, RegisteredBankAccount registeredBankAccount) {
        eventPublisher.publishEvent(new CheckedRegisteredBankAccountEvent(
                sagaId,
                payloadObject.getLoadMoneyRequestId(),
                registeredBankAccount.getId(),
                payloadObject.getMoneyId(),
                payloadObject.getMemberId(),
                "SUCCEEDED",
                payloadObject.getAmount(),
                registeredBankAccount.getBankName(),
                registeredBankAccount.getAccountNumber()
        ));
    }

    private void publishFailEvent(UUID sagaId, CheckRegisteredBankAccountEventPayload payload) {
        eventPublisher.publishEvent(new CheckedRegisteredBankAccountEvent(
                sagaId,
                payload.getLoadMoneyRequestId(),
                null,
                payload.getMoneyId(),
                payload.getMemberId(),
                "FAILED",
                payload.getAmount(),
                null,
                null
        ));
    }
}
