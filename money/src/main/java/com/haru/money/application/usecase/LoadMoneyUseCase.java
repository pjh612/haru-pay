package com.haru.money.application.usecase;

import com.haru.money.application.dto.LoadMoneyResponse;

import java.math.BigDecimal;
import java.util.UUID;

public interface LoadMoneyUseCase {
    LoadMoneyResponse loadMoney(UUID memberId, BigDecimal amount);
}
