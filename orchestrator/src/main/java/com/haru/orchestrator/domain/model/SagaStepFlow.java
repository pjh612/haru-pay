package com.haru.orchestrator.domain.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SagaStepFlow {

    private final Map<String, SagaStep> stepMap;
    private final SagaStep head;
    private final SagaStep tail;

    public SagaStepFlow() {
        this.stepMap = new ConcurrentHashMap<>();
        this.head = new SagaStep(null);
        this.tail = new SagaStep(null);
        this.head.setNext(tail);
        tail.setPrev(head);
    }

    public SagaStep getFirst() {
        return head.next();
    }

    private void addStep(String name) {
        SagaStep newNode = new SagaStep(name);


        SagaStep prev = tail.prev();
        tail.setPrev(newNode);
        prev.setNext(newNode);
        newNode.setNext(tail);
        newNode.setPrev(prev);

        stepMap.put(name, newNode);
    }

    public SagaStep getStep(String name) {
        SagaStep sagaStep = stepMap.get(name);
        if (sagaStep != null) {
            return sagaStep;
        }
        throw new IllegalArgumentException("Step not found: " + name);
    }

    public static class Builder {
        private final SagaStepFlow manager;

        public Builder() {
            manager = new SagaStepFlow();
        }

        public Builder addStep(String name) {
            manager.addStep(name);
            return this;
        }

        public SagaStepFlow build() {
            return manager;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
