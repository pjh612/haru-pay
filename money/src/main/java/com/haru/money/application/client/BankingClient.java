package com.haru.money.application.client;

import com.haru.money.application.client.dto.RegisteredBankAccountResponse;
import com.haru.money.application.client.dto.RequestFirmBankingRequest;
import com.haru.money.application.client.dto.RequestFirmBankingResponse;

import java.util.UUID;

public interface BankingClient {

    RegisteredBankAccountResponse getRegisteredBankAccount(UUID memberId);

    RequestFirmBankingResponse requestFirmBanking(UUID memberId, RequestFirmBankingRequest request);
}
