package com.haru.payments.adapter.out.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haru.payments.application.dto.PaymentResponse;
import com.haru.payments.application.dto.RequestPaymentResponse;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        RedisCacheConfiguration predefined = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, PaymentResponse.class))) // Value Serializer 변경
                .entryTtl(ClientTtlFunction.INSTANCE);

        RedisCacheConfiguration paymentRequest = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, RequestPaymentResponse.class))) // Value Serializer 변경
                .entryTtl(ClientTtlFunction.INSTANCE);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, Object.class))) // Value Serializer 변경
                .entryTtl(Duration.ofMinutes(5))
                .enableTimeToIdle();

        Map<String, RedisCacheConfiguration> initialCaches = Map.of("provisionalPayment", predefined, "paymentRequest", paymentRequest);
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(initialCaches)
                .build();
    }

    enum ClientTtlFunction implements RedisCacheWriter.TtlFunction {
        INSTANCE;

        @Override
        public Duration getTimeToLive(Object key, Object value) {
            return Duration.ofMinutes(10);
        }
    }

}
