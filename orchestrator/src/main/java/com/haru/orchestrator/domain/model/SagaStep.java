package com.haru.orchestrator.domain.model;

public class SagaStep {
    private final String topic;
    private SagaStep next;
    private SagaStep prev;

    public SagaStep(String topic) {
        this.topic = topic;
    }

    public String topic() {
        return this.topic;
    }

    public SagaStep next() {
        return this.next;
    }

    public SagaStep prev() {
        return this.prev;
    }

    void setPrev(SagaStep sagaStep) {
        this.prev = sagaStep;
    }

    void setNext(SagaStep newNode) {
        this.next = newNode;
    }
}
