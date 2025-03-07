package com.haru.money.adapters.out.client;


import com.haru.money.application.client.BankingClient;
import com.haru.money.application.client.dto.RegisteredBankAccountResponse;
import com.haru.money.application.client.dto.RequestFirmBankingRequest;
import com.haru.money.application.client.dto.RequestFirmBankingResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class BankingClientAdapter implements BankingClient {
    private final RestClient bankingClient;

    public BankingClientAdapter(@Qualifier("bankingClient") RestClient bankingClient) {
        this.bankingClient = bankingClient;
    }

    @Override
    public RegisteredBankAccountResponse getRegisteredBankAccount(UUID memberId) {
        return bankingClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/banking/account")
                        .queryParam("memberId", memberId)
                        .build())
                .retrieve()
                .body(RegisteredBankAccountResponse.class);
    }

    @Override
    public RequestFirmBankingResponse requestFirmBanking(UUID memberId, RequestFirmBankingRequest request) {
        return bankingClient.post()
                .uri("/api/firm-banking")
                .body(request)
                .retrieve()
                .body(RequestFirmBankingResponse.class);
    }
}
