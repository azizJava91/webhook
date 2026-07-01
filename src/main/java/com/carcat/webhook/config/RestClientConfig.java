package com.carcat.webhook.config;

import com.carcat.webhook.util.HmacSignatureValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    RestClient restClient(
            RestClient.Builder builder,
            CarlandProperties carlandProperties,
            WebhookSignatureProperties signatureProperties,
            HmacSignatureValidator hmacSignatureValidator
    ) {
        return builder
                .requestInterceptor((request, body, execution) -> {
                    if (StringUtils.hasText(carlandProperties.getInternalToken())) {
                        request.getHeaders().set(
                                CarlandProperties.INTERNAL_TOKEN_HEADER,
                                carlandProperties.getInternalToken()
                        );
                    }
                    if (StringUtils.hasText(signatureProperties.getSecret()) && body != null && body.length > 0) {
                        request.getHeaders().set(
                                HmacSignatureValidator.HEADER_NAME,
                                hmacSignatureValidator.sign(body)
                        );
                    }
                    return execution.execute(request, body);
                })
                .build();
    }
}
