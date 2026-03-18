package com.haru.payments.application.usecase.impl;

import com.haru.payments.domain.model.PaymentRequest;
import com.haru.payments.domain.repository.PaymentRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QueryPaymentStatusServiceMockTest {
    @InjectMocks
    private QueryPaymentStatusService queryPaymentStatusService;

    @Mock
    private PaymentRequestRepository paymentRequestRepository;

    @Test
    void query_ShouldReturnDurablePaymentState_WhenOwnedByClient() {
        UUID paymentId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        PaymentRequest paymentRequest = PaymentRequest.createNew(paymentId, "ORDER-1", UUID.randomUUID(), "product", BigDecimal.TEN, clientId);
        paymentRequest.success();

        when(paymentRequestRepository.findByIdAndClientId(paymentId, clientId)).thenReturn(Optional.of(paymentRequest));

        var response = queryPaymentStatusService.query(paymentId, clientId);

        assertThat(response.requestId()).isEqualTo(paymentId);
        assertThat(response.clientId()).isEqualTo(clientId);
        assertThat(response.paymentStatus()).isEqualTo(1);
        assertThat(response.approvedAt()).isNotNull();
        verify(paymentRequestRepository).findByIdAndClientId(paymentId, clientId);
    }

    @Test
    void query_ShouldRejectUnknownOrForeignPayment() {
        UUID paymentId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        when(paymentRequestRepository.findByIdAndClientId(paymentId, clientId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> queryPaymentStatusService.query(paymentId, clientId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("결제 정보를 찾을 수 없습니다.");
    }
}
