package com.haru.payments.adapter.out.client;

import com.haru.payments.application.client.BankingClient;
import com.haru.payments.application.client.dto.RegisteredBankAccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Component
public class BankingClientAdapter implements BankingClient {
    private final RestClient bankingRestClient;

    public BankingClientAdapter(@Qualifier("bankingRestClient") RestClient bankingRestClient) {
        this.bankingRestClient = bankingRestClient;
    }

    @Override
    public RegisteredBankAccountResponse getRegisteredBankAccount(UUID memberId) {
        return bankingRestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/banking/account").
                        queryParam("memberId", memberId)
                        .build())
                .retrieve()
                .body(RegisteredBankAccountResponse.class);
    }
}
