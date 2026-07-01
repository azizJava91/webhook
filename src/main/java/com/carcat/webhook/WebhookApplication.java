package com.carcat.webhook;

import com.carcat.webhook.config.CarlandProperties;
import com.carcat.webhook.config.RabbitProperties;
import com.carcat.webhook.config.WebhookSignatureProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({RabbitProperties.class, CarlandProperties.class, WebhookSignatureProperties.class})
public class WebhookApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebhookApplication.class, args);
	}

}
