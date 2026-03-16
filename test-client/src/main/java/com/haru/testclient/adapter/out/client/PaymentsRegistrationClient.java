package com.haru.testclient.adapter.out.client;

import com.haru.testclient.application.dto.ClientResponse;
import com.haru.testclient.application.dto.CreateClientRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PaymentsRegistrationClient {

    private final RestClient restClient;

    public PaymentsRegistrationClient(RestClient paymentsRestClient) {
        this.restClient = paymentsRestClient;
    }

    public ClientResponse registerClient(String name) {
        return restClient.post()
                .uri("/api/clients")
                .body(new CreateClientRequest(name))
                .retrieve()
                .body(ClientResponse.class);
    }
}
