package com.haru.orchestrator.application;

import com.haru.orchestrator.domain.model.PayloadType;

public interface SagaPayload {
    PayloadType type();

    Object toEvent();
}
