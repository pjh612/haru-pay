package com.haru.payments.application.usecase.impl;

import com.haru.payments.application.dto.CompletePaymentRequest;
import com.haru.payments.domain.model.PaymentRequest;
import com.haru.payments.domain.repository.PaymentRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmPaymentServiceMockTest {
    @InjectMocks
    private ConfirmPaymentService confirmPaymentService;

    @Mock
    private PaymentRequestRepository repository;

    @Test
    void confirm_ShouldMarkRequestAsSuccess_WhenRequestExists() {
        UUID requestId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        CompletePaymentRequest request = new CompletePaymentRequest(requestId);
        PaymentRequest paymentRequest = PaymentRequest.createNew(requestId, "ORDER-1", UUID.randomUUID(), "product", java.math.BigDecimal.TEN, clientId);

        when(repository.findById(requestId)).thenReturn(Optional.of(paymentRequest));
        when(repository.save(paymentRequest)).thenReturn(paymentRequest);

        var response = confirmPaymentService.confirm(request);

        assertThat(response.requestId()).isEqualTo(requestId);
        assertThat(response.paymentStatus()).isEqualTo(1);
        assertThat(paymentRequest.isSucceeded()).isTrue();
        verify(repository, times(1)).save(paymentRequest);
    }

    @Test
    void confirm_ShouldThrowException_WhenRequestDoesNotExist() {
        // Arrange
        UUID requestId = UUID.randomUUID();
        CompletePaymentRequest request = new CompletePaymentRequest(requestId);

        when(repository.findById(requestId)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThatThrownBy(() -> confirmPaymentService.confirm(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("결제 요청을 찾을 수 없습니다.");
    }

    @Test
    void failConfirm_ShouldMarkRequestAsFailed_WhenRequestExists() {
        UUID requestId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        PaymentRequest paymentRequest = PaymentRequest.createNew(requestId, "ORDER-1", UUID.randomUUID(), "product", java.math.BigDecimal.TEN, clientId);

        when(repository.findById(requestId)).thenReturn(Optional.of(paymentRequest));

        confirmPaymentService.failConfirm(requestId);

        assertThat(paymentRequest.isFailed()).isTrue();
        verify(repository, times(1)).save(paymentRequest);
    }

    @Test
    void failConfirm_ShouldDoNothing_WhenRequestDoesNotExist() {
        // Arrange
        UUID requestId = UUID.randomUUID();

        when(repository.findById(requestId)).thenReturn(Optional.empty());

        // Act
        confirmPaymentService.failConfirm(requestId);

        // Assert
        verify(repository, never()).save(any());
    }

    @Test
    void confirm_ShouldReturnCurrentState_WhenRequestAlreadySucceeded() {
        UUID requestId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        PaymentRequest paymentRequest = PaymentRequest.createNew(requestId, "ORDER-1", UUID.randomUUID(), "product", java.math.BigDecimal.TEN, clientId);
        paymentRequest.success();

        when(repository.findById(requestId)).thenReturn(Optional.of(paymentRequest));

        var response = confirmPaymentService.confirm(new CompletePaymentRequest(requestId));

        assertThat(response.requestId()).isEqualTo(requestId);
        verify(repository, never()).save(any());
    }

    @Test
    void confirm_ShouldThrowException_WhenRequestAlreadyFailed() {
        UUID requestId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        PaymentRequest paymentRequest = PaymentRequest.createNew(requestId, "ORDER-1", UUID.randomUUID(), "product", java.math.BigDecimal.TEN, clientId);
        paymentRequest.fail();

        when(repository.findById(requestId)).thenReturn(Optional.of(paymentRequest));

        Assertions.assertThatThrownBy(() -> confirmPaymentService.confirm(new CompletePaymentRequest(requestId)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 실패한 결제입니다.");

        verify(repository, never()).save(any());
    }

    @Test
    void failConfirm_ShouldDoNothing_WhenRequestAlreadySucceeded() {
        UUID requestId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        PaymentRequest paymentRequest = PaymentRequest.createNew(requestId, "ORDER-1", UUID.randomUUID(), "product", java.math.BigDecimal.TEN, clientId);
        paymentRequest.success();

        when(repository.findById(requestId)).thenReturn(Optional.of(paymentRequest));

        confirmPaymentService.failConfirm(requestId);

        verify(repository, never()).save(any());
    }

}
