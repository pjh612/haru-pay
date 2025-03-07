package com.haru.money.application.usecase.impl;

import com.fasterxml.uuid.Generators;
import com.haru.money.application.client.BankingClient;
import com.haru.money.application.client.dto.RegisteredBankAccountResponse;
import com.haru.money.application.client.dto.RequestFirmBankingRequest;
import com.haru.money.application.client.dto.RequestFirmBankingResponse;
import com.haru.money.application.dto.LoadMoneyResponse;
import com.haru.money.application.usecase.LoadMoneyUseCase;
import com.haru.money.common.lock.RedisLock;
import com.haru.money.domain.model.ChangingType;
import com.haru.money.domain.model.Money;
import com.haru.money.domain.model.MoneyChangingRequest;
import com.haru.money.domain.repository.MoneyChangingRequestRepository;
import com.haru.money.domain.repository.MoneyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoneyTransactionService implements LoadMoneyUseCase {
    private final MoneyChangingRequestRepository moneyChangingRequestRepository;
    private final MoneyRepository moneyRepository;
    private final BankingClient bankingClient;

    @Override
    @Transactional
    @RedisLock(waitTime = 60, key = "#memberId")
    public LoadMoneyResponse loadMoney(UUID memberId, BigDecimal amount) {
        UUID requestId = Generators.timeBasedEpochGenerator().generate();
        MoneyChangingRequest moneyChangingRequest = MoneyChangingRequest.createNew(
                requestId,
                memberId,
                ChangingType.INCREASE,
                amount
        );

        Money money = moneyRepository.findByMemberId(memberId).orElse(null);
        if (money == null) {
            return handleLoadMoneyFail(moneyChangingRequest, "Money not found for memberId: " + memberId);
        }

        RegisteredBankAccountResponse registeredBankAccount = bankingClient.getRegisteredBankAccount(memberId);
        if (registeredBankAccount == null) {
            return handleLoadMoneyFail(moneyChangingRequest, "Registered bank account not found for memberId: " + memberId);
        }

        RequestFirmBankingRequest firmBankingRequest = new RequestFirmBankingRequest(
                registeredBankAccount.bankName(),
                registeredBankAccount.accountNumber(),
                "harupay",
                "123123",
                amount
        );
        RequestFirmBankingResponse firmBankingResponse = bankingClient.requestFirmBanking(memberId, firmBankingRequest);
        if (!"SUCCEEDED".equals(firmBankingResponse.status())) {
            return handleLoadMoneyFail(moneyChangingRequest, "Firm banking request failed for memberId: " + memberId);
        }

        money.load(amount);
        moneyChangingRequest.success();

        moneyRepository.save(money);
        moneyChangingRequestRepository.save(moneyChangingRequest);

        return new LoadMoneyResponse(
                moneyChangingRequest.getId(),
                amount,
                moneyChangingRequest.getStatus().name(),
                Instant.now()
        );
    }

    private LoadMoneyResponse handleLoadMoneyFail(MoneyChangingRequest moneyChangingRequest, String reason) {
        log.error("Load money failed with reason: {}", reason);
        moneyChangingRequest.fail();
        moneyChangingRequestRepository.save(moneyChangingRequest);

        return new LoadMoneyResponse(
                moneyChangingRequest.getId(),
                moneyChangingRequest.getAmount(),
                moneyChangingRequest.getStatus().name(),
                Instant.now()
        );
    }

}
