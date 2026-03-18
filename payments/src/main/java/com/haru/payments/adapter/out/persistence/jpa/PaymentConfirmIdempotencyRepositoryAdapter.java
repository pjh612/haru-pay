package com.haru.payments.adapter.out.persistence.jpa;

import com.haru.payments.adapter.out.persistence.jpa.converter.PaymentConfirmIdempotencyConverter;
import com.haru.payments.adapter.out.persistence.jpa.entity.PaymentConfirmIdempotencyJpaEntity;
import com.haru.payments.domain.model.PaymentConfirmIdempotency;
import com.haru.payments.domain.repository.PaymentConfirmIdempotencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentConfirmIdempotencyRepositoryAdapter implements PaymentConfirmIdempotencyRepository {
    private final PaymentConfirmIdempotencyJpaRepository repository;

    @Override
    public PaymentConfirmIdempotency save(PaymentConfirmIdempotency paymentConfirmIdempotency) {
        PaymentConfirmIdempotencyJpaEntity entity = PaymentConfirmIdempotencyConverter.toEntity(paymentConfirmIdempotency);
        return PaymentConfirmIdempotencyConverter.toDomain(repository.save(entity));
    }

    @Override
    public Optional<PaymentConfirmIdempotency> findByClientIdAndIdempotencyKey(UUID clientId, String idempotencyKey) {
        return repository.findByClientIdAndIdempotencyKey(clientId, idempotencyKey)
                .map(PaymentConfirmIdempotencyConverter::toDomain);
    }
}
