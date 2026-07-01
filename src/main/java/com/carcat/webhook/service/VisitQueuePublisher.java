package com.carcat.webhook.service;

import com.carcat.webhook.config.RabbitProperties;
import com.carcat.webhook.messaging.QueuedVisitMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class VisitQueuePublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitProperties rabbitProperties;

    public void publish(HttpMethod method, String path, byte[] rawBody) {
        QueuedVisitMessage message = new QueuedVisitMessage(
                method.name(),
                path,
                new String(rawBody, StandardCharsets.UTF_8)
        );
        rabbitTemplate.convertAndSend(
                rabbitProperties.getExchange(),
                rabbitProperties.getRoutingKey(),
                message
        );
        log.info("Carland unavailable, queued {} {}", method, path);
    }
}
