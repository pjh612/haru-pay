package com.haru.payments.application.client;

import com.haru.payments.application.client.dto.RegisteredBankAccountResponse;

import java.util.UUID;

public interface BankingClient {
    RegisteredBankAccountResponse getRegisteredBankAccount(UUID memberId);
}
