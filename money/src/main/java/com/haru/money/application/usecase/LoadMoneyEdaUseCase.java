package com.haru.money.application.usecase;

import com.haru.money.application.dto.MoneyChangingResponse;

import java.math.BigDecimal;
import java.util.UUID;

public interface LoadMoneyEdaUseCase {
    void loadMoneySaga(String memberId, BigDecimal request);

    MoneyChangingResponse requestLoadMoney(UUID requestId, UUID memberId, BigDecimal amount);

    void failRequestLoadMoney(UUID requestId);

    void loadMoney(UUID requestId);

    void increaseMoney(UUID memberId, BigDecimal amount);
}
