package com.haru.payments.application.usecase.impl;

import com.fasterxml.uuid.Generators;
import com.haru.payments.application.dto.ClientResponse;
import com.haru.payments.application.dto.CreateClientRequest;
import com.haru.payments.application.usecase.CreateClientUseCase;
import com.haru.payments.domain.model.Client;
import com.haru.payments.domain.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateClientService implements CreateClientUseCase {
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ClientResponse create(CreateClientRequest request) {
        UUID id = Generators.timeBasedEpochGenerator().generate();
        UUID key = Generators.timeBasedEpochGenerator().generate();
        String encodedKey = passwordEncoder.encode(key.toString());
        Client client = Client.create(id, request.name(), encodedKey);
        Client savedClient = clientRepository.save(client);

        return new ClientResponse(savedClient.getId(),
                savedClient.getName(),
                key.toString(),
                savedClient.isActive(),
                savedClient.getCreatedAt());
    }
}
