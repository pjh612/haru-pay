package com.haru.orchestrator.application;

import com.haru.orchestrator.domain.model.SagaStepFlow;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SagaStepFlowRegistry {
    private final Map<String, SagaStepFlow> registry;

    public SagaStepFlowRegistry() {
        this.registry = new ConcurrentHashMap<>();
    }

    public void register(String name, SagaStepFlow stepFlow) {
        if(registry.containsKey(name)) {
            throw new IllegalArgumentException("SagaStepFlow with name " + name + " is already registered.");
        }

        registry.put(name, stepFlow);
    }

    public SagaStepFlow get(String name) {
        return registry.get(name);
    }
}
