package com.haru.money.application.usecase;

import com.haru.money.application.dto.DecreaseMoneyResponse;

import java.math.BigDecimal;
import java.util.UUID;

public interface DecreaseMoneyUseCase {
    DecreaseMoneyResponse decrease(UUID requestId, UUID memberId, BigDecimal amount);

    void onDecreaseFailed(UUID requestId, UUID memberId, BigDecimal amount);
}
