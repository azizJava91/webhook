package com.carcat.webhook.service;

import com.carcat.webhook.config.WebhookProperties;
import com.carcat.webhook.dto.VinExistsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
@RequiredArgsConstructor
public class VinCheckService {

    private final RestClient restClient;
    private final WebhookProperties properties;

    public VinExistsResponse checkVinExists(String vin) {
        String url = properties.getCarland().getBaseUrl()
                + properties.getCarland().getVinExistsPath()
                + "?vin=" + vin;

        try {
            Boolean exists = restClient.get()
                    .uri(url)
                    .header("X-Internal-Token", properties.getCarland().getInternalToken())
                    .retrieve()
                    .body(Boolean.class);

            return VinExistsResponse.builder()
                    .exists(Boolean.TRUE.equals(exists))
                    .build();
        } catch (RestClientException ex) {
            log.warn("VIN check failed against carland, returning false | vin={}, reason={}", vin, ex.getMessage());
            return VinExistsResponse.builder().exists(false).build();
        }
    }
}
