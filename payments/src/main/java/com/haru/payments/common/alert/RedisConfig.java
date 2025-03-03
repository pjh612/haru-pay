package com.haru.payments.common.alert;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(LettuceConnectionFactory lettuceConnectionFactory, MessageListener notificationMessageSubscriber) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.addMessageListener(notificationMessageSubscriber, new ChannelTopic(AlertChannel.PAYMENT_RESULT.name()));
        redisMessageListenerContainer.setConnectionFactory(lettuceConnectionFactory);

        return redisMessageListenerContainer;
    }
}
