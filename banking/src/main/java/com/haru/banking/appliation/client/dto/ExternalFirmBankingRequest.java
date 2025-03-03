package com.haru.banking.appliation.client.dto;

import java.math.BigDecimal;

public record ExternalFirmBankingRequest(
        String fromBankName,
        String fromBankAccountNumber,
        String toBankName,
        String toBankAccountNumber,
        BigDecimal amount
) {
}
