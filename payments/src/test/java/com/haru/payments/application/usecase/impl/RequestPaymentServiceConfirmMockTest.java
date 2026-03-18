package com.haru.payments.application.usecase.impl;

import com.haru.payments.application.cache.PaymentCacheRepository;
import com.haru.payments.application.client.BankingClient;
import com.haru.payments.application.client.MemberClient;
import com.haru.payments.application.client.MoneyClient;
import com.haru.payments.application.dto.PaymentCommand;
import com.haru.payments.application.event.ConfirmPaymentRequestEvent;
import com.haru.payments.domain.model.PaymentConfirmIdempotency;
import com.haru.payments.domain.model.PaymentRequest;
import com.haru.payments.domain.repository.PaymentConfirmIdempotencyRepository;
import com.haru.payments.domain.repository.PaymentRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestPaymentServiceConfirmMockTest {
    @InjectMocks
    private RequestPaymentService requestPaymentService;

    @Mock
    private MemberClient memberClient;
    @Mock
    private BankingClient bankingClient;
    @Mock
    private MoneyClient moneyClient;
    @Mock
    private PaymentRequestRepository paymentRequestRepository;
    @Mock
    private PaymentConfirmIdempotencyRepository paymentConfirmIdempotencyRepository;
    @Mock
    private PaymentCacheRepository paymentCacheRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    void confirmPayment_ShouldPublishEvent_WhenPaymentIsPendingAndOwnedByClient() {
        UUID paymentId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        PaymentRequest paymentRequest = PaymentRequest.createNew(paymentId, "ORDER-1", UUID.randomUUID(), "product", BigDecimal.TEN, clientId);

        when(paymentConfirmIdempotencyRepository.findByClientIdAndIdempotencyKey(clientId, "confirm-key-1")).thenReturn(Optional.empty());
        when(paymentConfirmIdempotencyRepository.save(any(PaymentConfirmIdempotency.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(paymentRequestRepository.findByIdAndClientId(paymentId, clientId)).thenReturn(Optional.of(paymentRequest));

        requestPaymentService.confirmPayment(new PaymentCommand(paymentId, clientId, "confirm-key-1"));

        verify(eventPublisher, times(1)).publishEvent(isA(ConfirmPaymentRequestEvent.class));
        verify(paymentConfirmIdempotencyRepository).save(any(PaymentConfirmIdempotency.class));
    }

    @Test
    void confirmPayment_ShouldIgnoreDuplicateRequest_WhenPaymentAlreadySucceeded() {
        UUID paymentId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        PaymentRequest paymentRequest = PaymentRequest.createNew(paymentId, "ORDER-1", UUID.randomUUID(), "product", BigDecimal.TEN, clientId);
        paymentRequest.success();

        when(paymentConfirmIdempotencyRepository.findByClientIdAndIdempotencyKey(clientId, "confirm-key-1")).thenReturn(Optional.empty());
        when(paymentRequestRepository.findByIdAndClientId(paymentId, clientId)).thenReturn(Optional.of(paymentRequest));

        requestPaymentService.confirmPayment(new PaymentCommand(paymentId, clientId, "confirm-key-1"));

        verify(eventPublisher, never()).publishEvent(any());
        verify(paymentConfirmIdempotencyRepository, never()).save(any(PaymentConfirmIdempotency.class));
    }

    @Test
    void confirmPayment_ShouldRejectRequest_WhenPaymentBelongsToAnotherClient() {
        UUID paymentId = UUID.randomUUID();
        UUID ownerClientId = UUID.randomUUID();
        UUID anotherClientId = UUID.randomUUID();
        PaymentRequest paymentRequest = PaymentRequest.createNew(paymentId, "ORDER-1", UUID.randomUUID(), "product", BigDecimal.TEN, ownerClientId);

        when(paymentConfirmIdempotencyRepository.findByClientIdAndIdempotencyKey(anotherClientId, "confirm-key-1")).thenReturn(Optional.empty());
        when(paymentRequestRepository.findByIdAndClientId(paymentId, anotherClientId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requestPaymentService.confirmPayment(new PaymentCommand(paymentId, anotherClientId, "confirm-key-1")))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("결제 정보를 찾을 수 없습니다.");

        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void confirmPayment_ShouldRejectRequest_WhenPaymentAlreadyFailed() {
        UUID paymentId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        PaymentRequest paymentRequest = PaymentRequest.createNew(paymentId, "ORDER-1", UUID.randomUUID(), "product", BigDecimal.TEN, clientId);
        paymentRequest.fail();

        when(paymentConfirmIdempotencyRepository.findByClientIdAndIdempotencyKey(clientId, "confirm-key-1")).thenReturn(Optional.empty());
        when(paymentRequestRepository.findByIdAndClientId(paymentId, clientId)).thenReturn(Optional.of(paymentRequest));

        assertThatThrownBy(() -> requestPaymentService.confirmPayment(new PaymentCommand(paymentId, clientId, "confirm-key-1")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 실패한 결제입니다.");

        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void confirmPayment_ShouldReturnWithoutPublishing_WhenSameIdempotencyKeyIsReplayed() {
        UUID paymentId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        when(paymentConfirmIdempotencyRepository.findByClientIdAndIdempotencyKey(clientId, "confirm-key-1"))
                .thenReturn(Optional.of(PaymentConfirmIdempotency.createNew(clientId, "confirm-key-1", paymentId)));

        requestPaymentService.confirmPayment(new PaymentCommand(paymentId, clientId, "confirm-key-1"));

        verify(paymentRequestRepository, never()).findByIdAndClientId(any(UUID.class), any(UUID.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void confirmPayment_ShouldRejectDifferentPayment_WhenSameIdempotencyKeyIsReused() {
        UUID paymentId = UUID.randomUUID();
        UUID otherPaymentId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        when(paymentConfirmIdempotencyRepository.findByClientIdAndIdempotencyKey(clientId, "confirm-key-1"))
                .thenReturn(Optional.of(PaymentConfirmIdempotency.createNew(clientId, "confirm-key-1", otherPaymentId)));

        assertThatThrownBy(() -> requestPaymentService.confirmPayment(new PaymentCommand(paymentId, clientId, "confirm-key-1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("동일 멱등성 키로 다른 결제를 확정할 수 없습니다.");

        verify(paymentRequestRepository, never()).findByIdAndClientId(any(UUID.class), any(UUID.class));
        verify(eventPublisher, never()).publishEvent(any());
    }
}
