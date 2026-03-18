package com.haru.payments.application.usecase.impl;

import com.haru.payments.application.dto.VerifyEmailRequest;
import com.haru.payments.application.port.out.cache.EmailVerificationTokenRepository;
import com.haru.payments.application.usecase.VerifyEmailUseCase;
import com.haru.payments.domain.model.Client;
import com.haru.payments.domain.repository.ClientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerifyEmailService implements VerifyEmailUseCase {
    private final ClientRepository clientRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Override
    @Transactional
    public void verify(VerifyEmailRequest request) {
        UUID clientId = emailVerificationTokenRepository.findClientIdByToken(request.token())
                .orElseThrow(() -> new EntityNotFoundException("유효하지 않은 인증 토큰입니다."));

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 클라이언트입니다."));

        client.verifyEmail();
        clientRepository.save(client);

        emailVerificationTokenRepository.delete(request.token());
    }
}
