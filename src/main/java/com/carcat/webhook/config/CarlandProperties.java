package com.carcat.webhook.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "webhook.carland")
public class CarlandProperties {

    public static final String INTERNAL_TOKEN_HEADER = "X-Internal-Token";

    private String baseUrl = "http://carland-service:9091";
    private String internalToken;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getInternalToken() {
        return internalToken;
    }

    public void setInternalToken(String internalToken) {
        this.internalToken = internalToken;
    }
}
