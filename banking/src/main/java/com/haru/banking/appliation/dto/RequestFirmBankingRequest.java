package com.haru.banking.appliation.dto;

import java.math.BigDecimal;

public record RequestFirmBankingRequest(
        String fromBankName,
        String fromBankAccountNumber,
        String toBankName,
        String toBankAccountNumber,
        BigDecimal amount
) {
}
