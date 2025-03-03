package com.haru.payments.adapter.out.client;

import com.haru.payments.application.client.MoneyClient;
import com.haru.payments.application.client.dto.MoneyResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;


@Component
public class MoneyClientAdapter implements MoneyClient {
    private final RestClient moneyRestClient;

    public MoneyClientAdapter(@Qualifier("moneyRestClient") RestClient moneyRestClient) {
        this.moneyRestClient = moneyRestClient;
    }

    @Override
    public MoneyResponse getMemberById(UUID uuid) {
        return moneyRestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/money")
                        .queryParam("memberId", uuid)
                        .build())
                .retrieve()
                .body(MoneyResponse.class);
    }
}
