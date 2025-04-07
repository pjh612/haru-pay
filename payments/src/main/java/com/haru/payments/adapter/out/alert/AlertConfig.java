package com.haru.payments.adapter.out.alert;

import com.alert.core.messaging.broadcaster.MessageConverter;
import com.alert.core.messaging.model.AlertMessage;
import com.alert.core.messaging.sender.AlertMessageDelegateSender;
import com.alert.core.messaging.sender.AlertMessageSender;
import com.alert.slack.SlackAlertMessageSender;
import com.alert.sse.EmitterRepository;
import com.alert.sse.SseAlertMessageSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AlertConfig {

    @Bean
    AlertMessageSender alertMessageSender(EmitterRepository emitterRepository, @Value("${alert.slack.webhook.url}") String webhookUrl, ObjectMapper objectMapper) {
        return new AlertMessageDelegateSender(List.of(new SseAlertMessageSender(emitterRepository), new SlackAlertMessageSender(webhookUrl, messageConverter(objectMapper), "#결제알림")));
    }

    MessageConverter<AlertMessage, String> messageConverter(ObjectMapper objectMapper) {
        return new ConfirmPaymentAlertFormatter(objectMapper);
    }
}
