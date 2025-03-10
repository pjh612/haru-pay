package com.haru.payments.adapter.in.event;

import com.haru.common.RequiresNewExecutor;
import lombok.experimental.WithBy;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.converter.BatchMessagingMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

@Configuration
public class EventKafkaConfig {
    @Bean
    StringJsonMessageConverter jsonMessageConverter() {
        return new StringJsonMessageConverter();
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(KafkaProperties props) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        factory.setConsumerFactory(consumerFactory(props));
        factory.setConcurrency(props.getListener().getConcurrency());
        factory.setRecordMessageConverter(jsonMessageConverter());
        factory.setBatchMessageConverter(new BatchMessagingMessageConverter(jsonMessageConverter()));
        return factory;
    }

    @Bean
    ConsumerFactory<String, String> consumerFactory(KafkaProperties props) {
        return new DefaultKafkaConsumerFactory<>(
                props.buildConsumerProperties(null),
                new StringDeserializer(),
                new StringDeserializer()
        );
    }

    @Bean
    RequiresNewExecutor requiresNewExecutor() {
        return new RequiresNewExecutor();
    }
}
