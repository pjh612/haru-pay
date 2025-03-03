package com.haru.banking.adapter.out.persistence.jpa;

import com.haru.banking.adapter.out.persistence.jpa.converter.RegisteredAccountConverter;
import com.haru.banking.adapter.out.persistence.jpa.entity.RegisteredAccountJpaEntity;
import com.haru.banking.domain.model.RegisteredBankAccount;
import com.haru.banking.domain.repository.RegisteredAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegisteredAccountRepositoryAdapter implements RegisteredAccountRepository {
    private final RegisteredAccountJpaRepository repository;

    @Override
    public Optional<RegisteredBankAccount> findByMemberId(UUID memberId) {
        return repository.findByMemberId(memberId)
                .map(RegisteredAccountConverter::toDomain);
    }

    @Override
    public RegisteredBankAccount save(RegisteredBankAccount registeredAccount) {
        RegisteredAccountJpaEntity entity = repository.save(RegisteredAccountConverter.toEntity(registeredAccount));

        return RegisteredAccountConverter.toDomain(entity);
    }
}
