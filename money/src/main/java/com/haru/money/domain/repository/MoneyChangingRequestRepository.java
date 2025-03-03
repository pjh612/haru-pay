package com.haru.money.domain.repository;

import com.haru.money.domain.model.MoneyChangingRequest;

import java.util.Optional;
import java.util.UUID;

public interface MoneyChangingRequestRepository {
    Optional<MoneyChangingRequest> findById(UUID id);

    MoneyChangingRequest save(MoneyChangingRequest moneyChangingRequest);
}
