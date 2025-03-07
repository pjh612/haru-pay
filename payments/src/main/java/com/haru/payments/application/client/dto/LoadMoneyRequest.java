package com.haru.payments.application.client.dto;

import java.math.BigDecimal;

public record LoadMoneyRequest(BigDecimal amount) {
}
