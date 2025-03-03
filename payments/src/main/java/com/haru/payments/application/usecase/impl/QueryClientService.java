package com.haru.payments.application.usecase.impl;

import com.haru.payments.application.dto.ClientResponse;
import com.haru.payments.application.usecase.QueryClientUseCase;
import com.haru.payments.domain.repository.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QueryClientService implements QueryClientUseCase {
    private final ClientRepository clientRepository;

    @Override
    public ClientResponse queryById(UUID id) {
        return clientRepository.findById(id)
                .map(it -> new ClientResponse(it.getId(), it.getName(), it.getApiKey(), it.isActive(), it.getCreatedAt()))
                .orElseThrow(() -> new EntityNotFoundException("클라이언트를 찾을 수 없습니다."));
    }
}
