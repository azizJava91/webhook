package com.carcat.webhook.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    RestClient restClient(RestClient.Builder builder, CarlandProperties carlandProperties) {
        return builder
                .requestInterceptor((request, body, execution) -> {
                    if (StringUtils.hasText(carlandProperties.getInternalToken())) {
                        request.getHeaders().set(
                                CarlandProperties.INTERNAL_TOKEN_HEADER,
                                carlandProperties.getInternalToken()
                        );
                    }
                    return execution.execute(request, body);
                })
                .build();
    }
}
