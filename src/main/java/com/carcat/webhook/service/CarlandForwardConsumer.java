package com.carcat.webhook.service;

import com.carcat.webhook.config.WebhookProperties;
import com.carcat.webhook.dto.WebhookEventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarlandForwardConsumer {

    private final RestClient restClient;
    private final WebhookProperties properties;

    @RabbitListener(queues = "${webhook.rabbit.queue}")
    public void forwardToCarland(WebhookEventMessage message) {
        String targetUrl = properties.getCarland().getBaseUrl() + "/internal/partner/webhook/relay";
        if (message.getQueryString() != null && !message.getQueryString().isBlank()) {
            targetUrl += "?" + message.getQueryString();
        }

        try {
            restClient.method(HttpMethod.valueOf(message.getMethod()))
                    .uri(targetUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Internal-Token", properties.getCarland().getInternalToken())
                    .header("X-Webhook-Event-Id", message.getEventId())
                    .header("X-Webhook-Original-Path", message.getPath())
                    .body(message.getBody() == null ? "" : message.getBody())
                    .retrieve()
                    .toBodilessEntity();

            log.info("Forwarded webhook event to carland | eventId={}, path={}", message.getEventId(), message.getPath());
        } catch (RestClientException ex) {
            log.error("Failed to forward webhook event, message will be retried | eventId={}, reason={}",
                    message.getEventId(), ex.getMessage());
            throw ex;
        }
    }
}
