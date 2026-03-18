package com.haru.payments.application.usecase.impl;

import com.fasterxml.uuid.Generators;
import com.haru.payments.application.dto.ClientResponse;
import com.haru.payments.application.dto.CreateClientRequest;
import com.haru.payments.application.port.out.cache.EmailVerificationTokenRepository;
import com.haru.payments.application.port.out.event.ClientEventPort;
import com.haru.payments.application.port.out.event.ClientEventPort;
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
    private final ClientEventPort clientEventPort;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Override
    @Transactional
    public ClientResponse create(CreateClientRequest request) {
        clientRepository.findByEmail(request.email()).ifPresent(c -> {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        });

        UUID id = Generators.timeBasedEpochGenerator().generate();
        UUID key = Generators.timeBasedEpochGenerator().generate();
        String verificationToken = UUID.randomUUID().toString();

        String encodedKey = passwordEncoder.encode(key.toString());
        String encodedPassword = passwordEncoder.encode(request.password());

        Client client = Client.create(id, request.email(), request.name(), encodedKey, encodedPassword);
        Client savedClient = clientRepository.save(client);

        emailVerificationTokenRepository.save(verificationToken, savedClient.getId());

        clientEventPort.sendEmailVerificationRequested(
                savedClient.getId(),
                savedClient.getEmail(),
                savedClient.getName(),
                verificationToken
        );

        return new ClientResponse(
                savedClient.getId(),
                savedClient.getName(),
                key.toString(),
                savedClient.isActive(),
                savedClient.getCreatedAt()
        );
    }
}
