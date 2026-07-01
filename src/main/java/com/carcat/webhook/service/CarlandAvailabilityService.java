package com.carcat.webhook.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarlandAvailabilityService {

    private static final String TEST_PATH = "/webhook/partner/test";

    private final RestClient restClient;
    private final com.carcat.webhook.config.CarlandProperties carlandProperties;

    public boolean isAvailable() {
        try {
            restClient.get()
                    .uri(carlandProperties.getBaseUrl() + TEST_PATH)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (ResourceAccessException ex) {
            log.debug("Carland unreachable: {}", ex.getMessage());
            return false;
        } catch (Exception ex) {
            log.debug("Carland health check failed: {}", ex.getMessage());
            return false;
        }
    }
}
