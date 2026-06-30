package com.carcat.webhook.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "webhook")
public class WebhookProperties {

    private Signature signature = new Signature();
    private Rabbit rabbit = new Rabbit();
    private Carland carland = new Carland();

    @Getter
    @Setter
    public static class Signature {
        private String secret;
    }

    @Getter
    @Setter
    public static class Rabbit {
        private String exchange = "webhook.events";
        private String queue = "webhook.to-carland";
        private String routingKey = "partner.event";
    }

    @Getter
    @Setter
    public static class Carland {
        private String baseUrl = "http://carland-service:9091";
        private String internalToken = "change-me";
        private String healthPath = "/actuator/health";
        private String vinExistsPath = "/internal/partner/cars/exists";
    }
}
