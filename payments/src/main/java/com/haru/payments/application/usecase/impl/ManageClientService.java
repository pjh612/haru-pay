package com.haru.payments.application.usecase.impl;

import com.fasterxml.uuid.Generators;
import com.haru.payments.application.dto.ClientResponse;
import com.haru.payments.application.usecase.ManageClientUseCase;
import com.haru.payments.domain.model.Client;
import com.haru.payments.domain.repository.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManageClientService implements ManageClientUseCase {
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ClientResponse regenerateApiKey(UUID clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("클��이언트를 찾을 수 없습니다."));

        UUID newKey = Generators.timeBasedEpochGenerator().generate();
        String encodedKey = passwordEncoder.encode(newKey.toString());
        
        Client updatedClient = new Client(
                client.getId(),
                client.getName(),
                encodedKey,
                client.isActive(),
                client.getCreatedAt()
        );
        
        Client savedClient = clientRepository.save(updatedClient);
        
        return new ClientResponse(
                savedClient.getId(),
                savedClient.getName(),
                newKey.toString(),
                savedClient.isActive(),
                savedClient.getCreatedAt()
        );
    }

    @Override
    @Transactional
    public void deactivateClient(UUID clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("클��이언트를 찾을 수 없습니다."));
        
        Client deactivatedClient = new Client(
                client.getId(),
                client.getName(),
                client.getApiKey(),
                false,
                client.getCreatedAt()
        );
        
        clientRepository.save(deactivatedClient);
    }

    @Override
    @Transactional
    public void activateClient(UUID clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("클��이언트를 찾을 수 없습니다."));
        
        Client activatedClient = new Client(
                client.getId(),
                client.getName(),
                client.getApiKey(),
                true,
                client.getCreatedAt()
        );
        
        clientRepository.save(activatedClient);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponse getClient(UUID clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("클��이언트를 찾을 수 없습니다."));
        
        return new ClientResponse(
                client.getId(),
                client.getName(),
                null,
                client.isActive(),
                client.getCreatedAt()
        );
    }
}
