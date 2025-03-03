package com.haru.money.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record MoneyInfoResponse(
        UUID memberId,
        BigDecimal balance
) {
}
