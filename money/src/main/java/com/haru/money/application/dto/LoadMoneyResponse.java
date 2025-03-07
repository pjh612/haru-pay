package com.haru.money.application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record LoadMoneyResponse(UUID id, BigDecimal amount, String status, Instant timestamp) {
}
