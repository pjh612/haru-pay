package com.haru.orchestrator.domain.model;

public enum SagaStepStatus {
    STARTED, FAILED, SUCCEEDED, COMPENSATING, COMPENSATED;

    public boolean isSucceeded() {
        return SUCCEEDED == this;
    }

    public boolean isFailedOrCompensated() {
        return this == FAILED || this == COMPENSATED;
    }

}
