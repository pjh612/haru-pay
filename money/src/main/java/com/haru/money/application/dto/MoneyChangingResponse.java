package com.haru.money.application.dto;

import com.haru.money.domain.model.MoneyChangingRequest;

import java.math.BigDecimal;
import java.util.UUID;

public record MoneyChangingResponse(UUID requestId, UUID memberId, BigDecimal amount) {

    public static MoneyChangingResponse of(MoneyChangingRequest request) {
        return new MoneyChangingResponse(
                request.getId(),
                request.getTargetMemberId(),
                request.getAmount()
        );
    }
}
