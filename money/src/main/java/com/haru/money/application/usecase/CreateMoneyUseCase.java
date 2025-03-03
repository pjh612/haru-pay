package com.haru.money.application.usecase;

import com.haru.money.application.dto.CreateMoneyRequest;

import java.util.UUID;

public interface CreateMoneyUseCase {
    UUID create(CreateMoneyRequest request);
}
