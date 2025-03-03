package com.haru.money.application.usecase;

import com.haru.money.application.dto.MoneyInfoResponse;

import java.util.UUID;

public interface QueryMoneyByMemberIdUseCase {
    MoneyInfoResponse query(UUID memberId);
}
