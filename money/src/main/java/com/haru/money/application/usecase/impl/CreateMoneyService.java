package com.haru.money.application.usecase.impl;

import com.haru.money.application.dto.CreateMoneyRequest;
import com.haru.money.application.usecase.CreateMoneyUseCase;
import com.haru.money.domain.model.Money;
import com.haru.money.domain.repository.MoneyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateMoneyService implements CreateMoneyUseCase {
    private final MoneyRepository moneyRepository;

    @Override
    public UUID create(CreateMoneyRequest request) {
        Money money = Money.createNew(request.memberId());
        moneyRepository.save(money);

        return money.getId();
    }
}
