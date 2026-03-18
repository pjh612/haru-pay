package com.haru.orchestrator.application;

import com.haru.orchestrator.adapter.in.event.payment.event.payload.DecreasedMoneyEventPayload;
import com.haru.orchestrator.adapter.in.event.payment.event.payload.PaymentConfirmRequestedEventPayload;
import com.haru.orchestrator.domain.model.EventLog;
import com.haru.orchestrator.domain.model.PayloadType;
import com.haru.orchestrator.domain.model.SagaStepFlow;
import com.haru.orchestrator.domain.model.SagaState;
import com.haru.orchestrator.domain.repository.EventLogRepository;
import com.haru.orchestrator.domain.repository.SagaStateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SagaTest {
    @Mock
    private SagaEventPublisher eventPublisher;
    @Mock
    private EventLogRepository eventLogRepository;
    @Mock
    private SagaStateRepository sagaStateRepository;

    private final JsonMapper jsonMapper = new JsonMapper();

    @Test
    void handle_shouldPersistNextStepPayloadAfterSuccess() {
        UUID sagaId = UUID.randomUUID();
        SagaState state = new SagaState(sagaId, "PAYMENT_SAGA",
                jsonMapper.valueToTree(new PaymentConfirmRequestedEventPayload(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, null, PayloadType.REQUEST)));

        Saga saga = new Saga(eventPublisher, jsonMapper, eventLogRepository, sagaStateRepository, state,
                SagaStepFlow.builder()
                        .addStep("payment-confirm-request")
                        .addStep("decrease-money")
                        .addStep("confirm-payment")
                        .build());

        saga.init(new PaymentConfirmRequestedEventPayload(state.getId(), UUID.randomUUID(), BigDecimal.TEN, null, PayloadType.REQUEST));
        when(eventLogRepository.findById(any())).thenReturn(null);

        saga.handle(UUID.randomUUID(), new PaymentConfirmRequestedEventPayload(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, null, PayloadType.SUCCEEDED));

        ArgumentCaptor<SagaState> captor = ArgumentCaptor.forClass(SagaState.class);
        verify(sagaStateRepository, org.mockito.Mockito.atLeast(2)).save(captor.capture());
        SagaState savedState = captor.getAllValues().getLast();

        assertThat(savedState.getCurrentStep()).isEqualTo("decrease-money");
        assertThat(savedState.getCurrentPayload().get("requestPrice").decimalValue()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(savedState.getCurrentPayload().has("balance")).isFalse();
        assertThat(savedState.getLastProgressAt()).isNotNull();
    }

    @Test
    void handle_shouldPersistCancelPayloadWhenCompensating() {
        UUID sagaId = UUID.randomUUID();
        SagaState state = new SagaState(sagaId, "PAYMENT_SAGA",
                jsonMapper.valueToTree(new PaymentConfirmRequestedEventPayload(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, null, PayloadType.REQUEST)));

        Saga saga = new Saga(eventPublisher, jsonMapper, eventLogRepository, sagaStateRepository, state,
                SagaStepFlow.builder()
                        .addStep("payment-confirm-request")
                        .addStep("decrease-money")
                        .addStep("confirm-payment")
                        .build());

        saga.init(new PaymentConfirmRequestedEventPayload(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, null, PayloadType.REQUEST));
        when(eventLogRepository.findById(any())).thenReturn(null);

        saga.handle(UUID.randomUUID(), new PaymentConfirmRequestedEventPayload(UUID.randomUUID(), UUID.randomUUID(), BigDecimal.TEN, null, PayloadType.SUCCEEDED));
        DecreasedMoneyEventPayload failedPayload = jsonMapper.convertValue(
                Map.of(
                        "requestId", UUID.randomUUID(),
                        "moneyChangingRequestId", UUID.randomUUID(),
                        "requestMemberId", UUID.randomUUID(),
                        "requestPrice", BigDecimal.TEN,
                        "balance", BigDecimal.ONE,
                        "failureReason", "timeout",
                        "type", PayloadType.FAILED
                ),
                DecreasedMoneyEventPayload.class
        );

        saga.handle(UUID.randomUUID(), failedPayload);

        ArgumentCaptor<SagaState> captor = ArgumentCaptor.forClass(SagaState.class);
        verify(sagaStateRepository, org.mockito.Mockito.atLeast(2)).save(captor.capture());
        SagaState savedState = captor.getAllValues().getLast();

        assertThat(savedState.getCurrentStep()).isEqualTo("payment-confirm-request");
        assertThat(savedState.getCurrentPayload().get("type").asText()).isEqualTo("CANCEL");
        assertThat(savedState.getCurrentPayload().get("failureReason").asText()).isEqualTo("timeout");
    }
}
