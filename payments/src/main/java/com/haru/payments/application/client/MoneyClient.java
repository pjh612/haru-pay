package com.haru.payments.application.client;

import com.haru.payments.application.client.dto.MoneyResponse;

import java.util.UUID;

public interface MoneyClient {
    MoneyResponse getMemberById(UUID uuid);
}
