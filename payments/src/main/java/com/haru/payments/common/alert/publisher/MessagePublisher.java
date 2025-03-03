package com.haru.payments.common.alert.publisher;


import com.haru.payments.common.alert.MessageDto;

public interface MessagePublisher {
    void publish(String channel, MessageDto messageDto);
}
