package com.haru.payments.adapter.out.event;

import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class EventProducerConfig {

    @Bean
    ProducerFactory<String, Object> eventProducerFactory(KafkaProperties props) {
        return new DefaultKafkaProducerFactory<>(
                props.buildProducerProperties(),
                new StringSerializer(),
                new JsonSerializer<>()
        );
    }

    @Bean
    KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> eventProducerFactory) {
        return new KafkaTemplate<>(eventProducerFactory);
    }
}
