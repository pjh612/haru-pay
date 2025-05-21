package com.haru.payments.config;

import com.haru.common.lock.RedisLockAspect;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisLockConfig {
    @Bean
    RedisLockAspect redisLockAspect(RedissonClient redisson, ApplicationEventPublisher publisher) {
        return new RedisLockAspect(redisson, publisher);
    }
}
