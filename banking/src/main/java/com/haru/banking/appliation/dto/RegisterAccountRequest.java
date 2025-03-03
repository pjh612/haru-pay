package com.haru.banking.appliation.dto;

import java.util.UUID;

public record RegisterAccountRequest(
        UUID memberId,
        String bankName,
        String bankAccountNumber,
        String isValid
) {
}
