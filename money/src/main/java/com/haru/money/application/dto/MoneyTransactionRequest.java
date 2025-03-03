package com.haru.money.application.dto;

import java.math.BigDecimal;

public record MoneyTransactionRequest(BigDecimal amount) {
}
