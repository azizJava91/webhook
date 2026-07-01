package com.carcat.webhook.util;

public final class WebhookHeaders {

    public static final String DELIVERY_SOURCE = "X-Webhook-Delivery";
    public static final String DELIVERY_RABBIT_REPLAY = "rabbit-replay";

    private WebhookHeaders() {
    }
}
