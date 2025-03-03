package com.haru.common.hibernate;

import com.fasterxml.uuid.Generators;
import jakarta.persistence.Id;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;

import java.util.EnumSet;
import java.util.UUID;

import static org.hibernate.generator.EventTypeSets.INSERT_ONLY;

@Slf4j
public class UuidV7Generator implements BeforeExecutionGenerator {
    @Override
    public EnumSet<EventType> getEventTypes() {
        return INSERT_ONLY;
    }

    @Override
    public boolean allowAssignedIdentifiers() {
        return true;
    }


    @Override
    public Object generate(SharedSessionContractImplementor session, Object entity, Object currentValue, EventType eventType) {
        Object entityIdType = getEntityIdType(entity);
        Object entityIdValue = getEntityIdValue(entity);


        if (entityIdValue == null) {
            UUID uuid = Generators.timeBasedEpochGenerator().generate();
            if (entityIdType.equals(String.class) ) {
                return uuid.toString();
            } else if (entityIdType.equals(UUID.class)) {
                return uuid;
            }

            throw new IllegalArgumentException("Unsupported entity ID type: " + entityIdType);
        } else {
            return entityIdValue;
        }
    }

    private Object getEntityIdValue(Object entity) {
        try {
            for (var field : entity.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    return field.get(entity);
                }
            }
        } catch (IllegalAccessException e) {
            log.error("Failed to access ID field in entity: {}", entity.getClass().getName(), e);
        }
        return null;
    }

    private Object getEntityIdType(Object entity) {
        for (var field : entity.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                return field.getType();
            }
        }
        throw new IllegalArgumentException("Id annotation not found in entity");
    }
}
