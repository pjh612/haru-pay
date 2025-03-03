package com.haru.orchestrator.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haru.orchestrator.domain.model.SagaState;
import com.haru.orchestrator.domain.repository.EventLogRepository;
import com.haru.orchestrator.domain.repository.SagaStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SagaFactory {
    private final EventLogRepository eventLogRepository;
    private final SagaEventPublisher eventPublisher;
    private final SagaStateRepository sagaStateRepository;
    private final ObjectMapper objectMapper;
    private final SagaStepFlowRegistry sagaStepFlowRegistry;

    public Saga createInstance(UUID sagaId) {
        return createInstance(sagaId, null, null);
    }

    public Saga createInstance(UUID sagaId, String sagaType, JsonNode payload) {
        SagaState state = findOrCreateSagaState(sagaId, sagaType, payload);

        return new Saga(eventPublisher, objectMapper, eventLogRepository, sagaStateRepository, state, sagaStepFlowRegistry.get(state.getType()));
    }

    private SagaState findOrCreateSagaState(UUID sagaId, String sagaType, JsonNode payload) {
        return sagaStateRepository.findById(sagaId)
                .orElseGet(() -> sagaStateRepository.save(new SagaState(sagaId, sagaType, payload)));
    }

}
