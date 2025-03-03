package com.haru.orchestrator.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SagaManager {

    private final SagaFactory sagaFactory;
    private final ObjectMapper objectMapper;

    @Transactional
    public void begin(UUID sagaId, String sagaType, Object payload) {
        JsonNode jsonNode = objectMapper.convertValue(payload, JsonNode.class);
        Saga saga = sagaFactory.createInstance(sagaId, sagaType, jsonNode);
        saga.init(jsonNode);

    }

    @Transactional
    public void handle(UUID id, UUID eventId, SagaPayload payload) {
        Saga saga = sagaFactory.createInstance(id);
        saga.handle(eventId, payload);
    }
}
