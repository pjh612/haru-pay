package com.haru.money.application.usecase.impl;

import com.fasterxml.uuid.Generators;
import com.haru.money.adapters.in.event.loadmoney.LoadMoneyFinishedEvent;
import com.haru.money.adapters.in.event.loadmoney.LoadMoneyRequestCreatedEvent;
import com.haru.money.application.dto.DecreaseMoneyResponse;
import com.haru.money.application.dto.MoneyChangingResponse;
import com.haru.money.application.event.LoadMoneyRequestEvent;
import com.haru.money.application.usecase.DecreaseMoneyUseCase;
import com.haru.money.application.usecase.LoadMoneyEdaUseCase;
import com.haru.money.common.lock.RedisLock;
import com.haru.money.domain.model.ChangingType;
import com.haru.money.domain.model.Money;
import com.haru.money.domain.model.MoneyChangingRequest;
import com.haru.money.domain.repository.MoneyChangingRequestRepository;
import com.haru.money.domain.repository.MoneyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MoneyTransactionEdaService implements LoadMoneyEdaUseCase, DecreaseMoneyUseCase {
    private final MoneyChangingRequestRepository moneyChangingRequestRepository;
    private final MoneyRepository moneyRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void loadMoneySaga(String memberId, BigDecimal amount) {
        LoadMoneyRequestEvent event = new LoadMoneyRequestEvent(
                Generators.timeBasedEpochGenerator().generate(),
                memberId,
                amount);

        eventPublisher.publishEvent(event);
    }

    @Override
    @Transactional
    public MoneyChangingResponse requestLoadMoney(UUID requestId, UUID memberId, BigDecimal amount) {
        MoneyChangingRequest moneyChangingRequest = MoneyChangingRequest.createNew(
                requestId,
                memberId,
                ChangingType.INCREASE,
                amount
        );
        moneyRepository.findByMemberId(memberId)
                .ifPresentOrElse(
                        money -> eventPublisher.publishEvent(LoadMoneyRequestCreatedEvent.success(requestId,
                                money.getId(),
                                money.getMemberId(),
                                moneyChangingRequest.getAmount())),
                        () -> {
                            moneyChangingRequest.fail();
                            eventPublisher.publishEvent(LoadMoneyRequestCreatedEvent.fail(requestId));
                        }
                );
        MoneyChangingRequest save = moneyChangingRequestRepository.save(moneyChangingRequest);

        return MoneyChangingResponse.of(save);
    }

    @Transactional
    @Override
    public void failRequestLoadMoney(UUID requestId) {
        MoneyChangingRequest moneyChangingRequest = moneyChangingRequestRepository.findById(requestId)
                .orElseThrow(RuntimeException::new);

        moneyChangingRequest.fail();
        moneyChangingRequestRepository.save(moneyChangingRequest);
        eventPublisher.publishEvent(LoadMoneyRequestCreatedEvent.fail(requestId));

    }

    @Transactional
    @Override
    public void loadMoney(UUID requestId) {
        MoneyChangingRequest moneyChangingRequest = moneyChangingRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("MoneyChangingRequest not found for ID: " + requestId));
        increaseMoney(moneyChangingRequest.getTargetMemberId(), moneyChangingRequest.getAmount());
        moneyChangingRequest.success();
        moneyChangingRequestRepository.save(moneyChangingRequest);

        eventPublisher.publishEvent(LoadMoneyFinishedEvent.success(requestId));
    }

    @Transactional
    @Override
    @RedisLock(waitTime = 60, key = "#memberId")
    public void increaseMoney(UUID memberId, BigDecimal amount) {
        Money money = moneyRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("Money not found for Member ID: " + memberId));
        money.load(amount);
        moneyRepository.save(money);
    }

    @Override
    @Transactional
    @RedisLock(waitTime = 60, key = "#memberId")
    public DecreaseMoneyResponse decrease(UUID requestId, UUID memberId, BigDecimal amount) {
        MoneyChangingRequest newMoneyChangingRequest = MoneyChangingRequest.createNew(requestId, memberId, ChangingType.DECREASE, amount);
        Money money = findMoneyAndDecrease(memberId, newMoneyChangingRequest);
        newMoneyChangingRequest.success();
        MoneyChangingRequest moneyChangingRequest = moneyChangingRequestRepository.save(newMoneyChangingRequest);

        return new DecreaseMoneyResponse(memberId, moneyChangingRequest.getId(), moneyChangingRequest.getStatus(), money.getBalance());
    }

    private Money findMoneyAndDecrease(UUID memberId, MoneyChangingRequest newMoneyChangingRequest) {
        Money foundMoney = moneyRepository.findByMemberId(memberId)
                .orElseThrow(EntityNotFoundException::new);

        foundMoney.decrease(newMoneyChangingRequest.getAmount());
        return moneyRepository.save(foundMoney);
    }

    @Override
    @Transactional
    public void onDecreaseFailed(UUID requestId, UUID memberId, BigDecimal amount) {
        MoneyChangingRequest foundMoneyChangingRequest = moneyChangingRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("요청 정보를 찾을 수 없습니다."));
        rollbackDecreasedMoney(memberId, amount);
        foundMoneyChangingRequest.fail();
        moneyChangingRequestRepository.save(foundMoneyChangingRequest);
    }

    private void rollbackDecreasedMoney(UUID memberId, BigDecimal amount) {
        Money foundMoney = moneyRepository.findByMemberId(memberId)
                .orElseThrow(EntityNotFoundException::new);
        foundMoney.load(amount);
        moneyRepository.save(foundMoney);
    }

}
