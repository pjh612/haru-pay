package com.haru.money.domain.repository;

import com.haru.money.domain.model.Money;

import java.util.Optional;
import java.util.UUID;

public interface MoneyRepository {
    Money save(Money money);

    Optional<Money> findByMemberId(UUID memberId);
}
