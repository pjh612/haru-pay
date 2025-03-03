package com.haru.banking.appliation.dto;

import java.util.UUID;

public record QueryRegisteredBankAccountResponse(UUID id, String bankName, String accountNumber) {
}
