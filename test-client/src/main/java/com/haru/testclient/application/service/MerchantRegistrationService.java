package com.haru.testclient.application.service;

import com.haru.testclient.adapter.out.client.PaymentsRegistrationClient;
import com.haru.testclient.application.dto.ClientResponse;
import com.haru.testclient.domain.model.MerchantSession;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MerchantRegistrationService {

    private final PaymentsRegistrationClient registrationClient;
    private final Map<String, MerchantSession> sessions = new ConcurrentHashMap<>();

    public MerchantRegistrationService(PaymentsRegistrationClient registrationClient) {
        this.registrationClient = registrationClient;
    }

    public MerchantSession registerMerchant(String name) {
        ClientResponse response = registrationClient.registerClient(name);
        
        MerchantSession session = new MerchantSession(
                response.id(),
                response.name(),
                response.apiKey(),
                Instant.now()
        );
        
        sessions.put(response.id().toString(), session);
        return session;
    }

    public MerchantSession getSession(String clientId) {
        return sessions.get(clientId);
    }

    public void clearSession(String clientId) {
        sessions.remove(clientId);
    }
}
