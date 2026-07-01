package com.carcat.webhook.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueuedVisitMessage {
    private String httpMethod;
    private String path;
    private String body;
}
