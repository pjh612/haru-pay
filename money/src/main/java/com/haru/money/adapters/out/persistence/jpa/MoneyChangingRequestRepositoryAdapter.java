package com.haru.money.adapters.out.persistence.jpa;

import com.haru.money.adapters.out.persistence.jpa.converter.MoneyChangingRequestConverter;
import com.haru.money.adapters.out.persistence.jpa.entity.MoneyChangingRequestJpaEntity;
import com.haru.money.domain.model.MoneyChangingRequest;
import com.haru.money.domain.repository.MoneyChangingRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MoneyChangingRequestRepositoryAdapter implements MoneyChangingRequestRepository {
    private final MoneyChangingRequestJpaRepository repository;

    @Override
    public Optional<MoneyChangingRequest> findById(UUID id) {
        return repository.findById(id)
                .map(MoneyChangingRequestConverter::toDomain);
    }

    @Override
    public MoneyChangingRequest save(MoneyChangingRequest wallet) {
        MoneyChangingRequestJpaEntity walletEntity = MoneyChangingRequestConverter.toEntity(wallet);

        return MoneyChangingRequestConverter.toDomain(repository.save(walletEntity));
    }
}
