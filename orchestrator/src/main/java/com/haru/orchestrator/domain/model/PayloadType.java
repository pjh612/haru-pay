package com.haru.orchestrator.domain.model;

public enum PayloadType {
    REQUEST, SUCCEEDED, FAILED, CANCEL;

    public boolean isSucceeded() {
        return this == SUCCEEDED;
    }
}
