package com.haru.payments.application.port.out.client;

import com.haru.payments.application.client.dto.RegisteredBankAccountResponse;

import java.util.UUID;

public interface BankingClient {
    RegisteredBankAccountResponse getRegisteredBankAccount(UUID memberId);
}
