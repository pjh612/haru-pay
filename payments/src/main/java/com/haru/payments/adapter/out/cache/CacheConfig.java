package com.haru.payments.adapter.out.cache;

import com.haru.payments.application.dto.PaymentResponse;
import com.haru.payments.application.dto.RequestPaymentResponse;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, JsonMapper jsonMapper) {
        JsonMapper redisMapper = jsonMapper.rebuild().build();
        
        GenericJacksonJsonRedisSerializer jsonSerializer = new GenericJacksonJsonRedisSerializer(redisMapper);
        
        RedisCacheConfiguration predefined = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
                .entryTtl(ClientTtlFunction.INSTANCE);

        RedisCacheConfiguration paymentRequest = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
                .entryTtl(ClientTtlFunction.INSTANCE);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
                .entryTtl(Duration.ofMinutes(5))
                .enableTimeToIdle();

        Map<String, RedisCacheConfiguration> initialCaches = Map.of(
            "provisionalPayment", predefined, 
            "paymentRequest", paymentRequest
        );
        
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
