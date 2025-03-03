package com.haru.auth.application.service;

import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientService {
    private final RegisteredClientRepository clientRepository;

    public ClientService(RegisteredClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional
    public void save(RegisteredClient registeredClient) {
        clientRepository.save(registeredClient);
    }
}
