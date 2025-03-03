package com.haru.payments.adapter.out.persistence.jpa;

import com.haru.payments.adapter.out.persistence.jpa.converter.PaymentRequestConverter;
import com.haru.payments.adapter.out.persistence.jpa.entity.PaymentRequestJpaEntity;
import com.haru.payments.domain.model.PaymentRequest;
import com.haru.payments.domain.repository.PaymentRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentRequestRepositoryAdapter implements PaymentRequestRepository {
    private final PaymentRequestJpaRepository repository;

    @Override
    public PaymentRequest save(PaymentRequest paymentRequest) {
        PaymentRequestJpaEntity entity = PaymentRequestConverter.toEntity(paymentRequest);
        return PaymentRequestConverter.toDomain(repository.save(entity));
    }

    @Override
    public Optional<PaymentRequest> findById(UUID paymentRequestId) {
        return repository.findById(paymentRequestId)
                .map(PaymentRequestConverter::toDomain);
    }
}
