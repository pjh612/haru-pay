package com.haru.payments.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentPageResponse(
        UUID requestId,
        String orderId,
        BigDecimal requestPrice,
        String clientName,
        BigDecimal moneyBalance,
        BigDecimal shortfallAmount,
        String registeredBankAccountId,
        String registeredBankName,
        String registeredBankAccountNumber
) {
}
