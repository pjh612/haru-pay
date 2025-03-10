package com.haru.money.adapters.in.event;

import com.haru.common.RequiresNewExecutor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.converter.BatchMessagingMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

@EnableKafka
@Configuration
public class EventKafkaConfig {
    @Bean
    StringJsonMessageConverter jsonMessageConverter() {
        return new StringJsonMessageConverter();
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
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(KafkaProperties props) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        factory.setConsumerFactory(consumerFactory(props));
        factory.setRecordMessageConverter(jsonMessageConverter());
        factory.setBatchMessageConverter(new BatchMessagingMessageConverter(jsonMessageConverter()));
        factory.setConcurrency(props.getListener().getConcurrency());
        return factory;
    }

    @Bean
    RequiresNewExecutor requiresNewExecutor() {
        return new RequiresNewExecutor();
    }
}
