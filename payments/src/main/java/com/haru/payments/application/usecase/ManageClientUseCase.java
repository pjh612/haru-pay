package com.haru.payments.application.usecase;

import com.haru.payments.application.dto.ClientResponse;

import java.util.UUID;

public interface ManageClientUseCase {
    ClientResponse regenerateApiKey(UUID clientId);
    
    void deactivateClient(UUID clientId);
    
    void activateClient(UUID clientId);
    
    ClientResponse getClient(UUID clientId);
}
