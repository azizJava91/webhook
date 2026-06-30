package com.carcat.webhook.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookEventMessage {

    private String eventId;
    private String method;
    private String path;
    private String queryString;
    private Map<String, String> headers;
    private String body;
    private Instant receivedAt;
}
