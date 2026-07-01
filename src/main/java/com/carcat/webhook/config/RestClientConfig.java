package com.carcat.webhook.config;

import com.carcat.webhook.util.HmacSignatureValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean
    RestClient restClient(
            RestClient.Builder builder,
            CarlandProperties carlandProperties,
            WebhookSignatureProperties signatureProperties,
            HmacSignatureValidator hmacSignatureValidator,
            @Value("${webhook.carland.connect-timeout-ms:5000}") int connectTimeoutMs,
            @Value("${webhook.carland.read-timeout-ms:10000}") int readTimeoutMs
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(connectTimeoutMs));
        requestFactory.setReadTimeout(Duration.ofMillis(readTimeoutMs));

        return builder
                .requestFactory(requestFactory)
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
