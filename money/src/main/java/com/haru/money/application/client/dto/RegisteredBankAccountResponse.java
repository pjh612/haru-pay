package com.haru.money.application.client.dto;

import java.util.UUID;

public record RegisteredBankAccountResponse(UUID id, String bankName, String accountNumber) {
}
