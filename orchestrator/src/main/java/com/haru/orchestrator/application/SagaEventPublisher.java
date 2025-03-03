package com.haru.orchestrator.application;

import com.haru.orchestrator.domain.model.SagaEvent;

public interface SagaEventPublisher {

    void publishEvent(SagaEvent sagaEvent);
}
