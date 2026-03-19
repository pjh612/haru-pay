package com.haru.testclient.application.service;

import com.haru.testclient.domain.model.MerchantSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class MerchantRegistrationService {

    private final MerchantSession fixedMerchant;

    public MerchantRegistrationService(
            @Value("${merchant.client-id}") UUID clientId,
            @Value("${merchant.client-name}") String clientName,
            @Value("${merchant.api-key}") String apiKey) {
        this.fixedMerchant = new MerchantSession(clientId, clientName, apiKey, Instant.EPOCH);
    }

    public MerchantSession getMerchant() {
        return fixedMerchant;
    }
}
