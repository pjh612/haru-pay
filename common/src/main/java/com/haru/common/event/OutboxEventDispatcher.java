package com.haru.common.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.MANDATORY;

class OutboxEventDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(OutboxEventDispatcher.class);

    private final EntityManager entityManager;
    private final ObjectMapper objectMapper;
    private final boolean removeAfterInsert;

    OutboxEventDispatcher(EntityManager entityManager, boolean removeAfterInsert, ObjectMapper objectMapper) {
        this.entityManager = entityManager;
        this.removeAfterInsert = removeAfterInsert;
        this.objectMapper = objectMapper;
    }

    @EventListener
    @Transactional(propagation = MANDATORY)
    public void on(OutboxEvent<?, ?> event) {
        try (var session = entityManager.unwrap(Session.class)) {
            logger.info("An exported event was found for type {}", event.type());

            // Unwrap to Hibernate session and save
            ObjectNode jsonNodes = objectMapper.convertValue(event.payload(), ObjectNode.class);
            String payload = objectMapper.writeValueAsString(jsonNodes);

            var outbox = new Outbox(event, payload);
            session.persist(outbox);

            logger.info("persist after");
            // Remove entity if the configuration deems doing so, leaving useful
            // for debugging
            if (removeAfterInsert) {
                session.remove(outbox);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
