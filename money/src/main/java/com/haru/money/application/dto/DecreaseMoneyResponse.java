package com.haru.money.application.dto;

import com.haru.money.domain.model.ChangingStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record DecreaseMoneyResponse(
        UUID memberId,
        UUID requestId,
        ChangingStatus status,
        BigDecimal balance
) {
}
