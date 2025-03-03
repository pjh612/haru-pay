package com.haru.common.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan("com.haru.common.event")
public class OutboxConfig {

    @Bean
    OutboxEventDispatcher outboxEventDispatcher(EntityManager entityManager, ObjectMapper objectMapper) {
        return new OutboxEventDispatcher(entityManager, false, objectMapper);
    }
}
