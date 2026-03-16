package com.haru.orchestrator.application;

import com.haru.orchestrator.domain.model.SagaState;
import com.haru.orchestrator.domain.repository.EventLogRepository;
import com.haru.orchestrator.domain.repository.SagaStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SagaFactory {
    private final EventLogRepository eventLogRepository;
    private final SagaEventPublisher eventPublisher;
    private final SagaStateRepository sagaStateRepository;
    private final JsonMapper jsonMapper;
    private final SagaStepFlowRegistry sagaStepFlowRegistry;

    public Saga createInstance(UUID sagaId) {
        return createInstance(sagaId, null, null);
    }

    public Saga createInstance(UUID sagaId, String sagaType, JsonNode payload) {
        SagaState state = findOrCreateSagaState(sagaId, sagaType, payload);

        return new Saga(eventPublisher, jsonMapper, eventLogRepository, sagaStateRepository, state, sagaStepFlowRegistry.get(state.getType()));
    }

    private SagaState findOrCreateSagaState(UUID sagaId, String sagaType, JsonNode payload) {
        return sagaStateRepository.findById(sagaId)
                .orElseGet(() -> sagaStateRepository.save(new SagaState(sagaId, sagaType, payload)));
    }

}
