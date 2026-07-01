package com.carcat.webhook.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "webhook.rabbit")
public class RabbitProperties {

    private String exchange = "webhook.events";
    private String queue = "webhook.to-carland";
    private String routingKey = "partner.event";
    private String listenerId = "carlandVisitQueueConsumer";
    private long pollIntervalMs = 30_000L;

    public String getListenerId() {
        return listenerId;
    }

    public void setListenerId(String listenerId) {
        this.listenerId = listenerId;
    }

    public long getPollIntervalMs() {
        return pollIntervalMs;
    }

    public void setPollIntervalMs(long pollIntervalMs) {
        this.pollIntervalMs = pollIntervalMs;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }
}
