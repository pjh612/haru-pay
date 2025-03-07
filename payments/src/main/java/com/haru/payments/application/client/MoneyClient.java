package com.haru.payments.application.client;

import com.haru.payments.application.client.dto.LoadMoneyResponse;
import com.haru.payments.application.client.dto.MoneyResponse;

import java.math.BigDecimal;
import java.util.UUID;

public interface MoneyClient {
    MoneyResponse getMemberById(UUID uuid);

    LoadMoneyResponse loadMoney(UUID uuid, BigDecimal loadAmount);
}
