package com.carcat.webhook.service;

import com.carcat.webhook.config.WebhookProperties;
import com.carcat.webhook.dto.WebhookEventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final WebhookProperties properties;

    public String publish(String method, String path, String queryString, Map<String, String> headers, String body) {
        String eventId = UUID.randomUUID().toString();
        WebhookEventMessage message = WebhookEventMessage.builder()
                .eventId(eventId)
                .method(method)
                .path(path)
                .queryString(queryString)
                .headers(headers)
                .body(body)
                .receivedAt(Instant.now())
                .build();

        rabbitTemplate.convertAndSend(
                properties.getRabbit().getExchange(),
                properties.getRabbit().getRoutingKey(),
                message);

        log.info("Published webhook event | eventId={}, method={}, path={}", eventId, method, path);
        return eventId;
    }
}
