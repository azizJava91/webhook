package com.carcat.webhook.service;

import com.carcat.webhook.config.CarlandProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class CarlandClientService {

    private static final String PARTNER_BASE = "/webhook/partner";

    private final RestClient restClient;
    private final CarlandProperties carlandProperties;

    public String fetchTestResponse() {
        return restClient.get()
                .uri(carlandProperties.getBaseUrl() + PARTNER_BASE + "/test")
                .retrieve()
                .body(String.class);
    }

    public boolean findCarByVin(String vin) {
        String uri = UriComponentsBuilder
                .fromHttpUrl(carlandProperties.getBaseUrl() + PARTNER_BASE + "/car/find")
                .queryParam("vin", vin)
                .toUriString();

        Boolean exists = restClient.get()
                .uri(uri)
                .retrieve()
                .body(Boolean.class);

        return Boolean.TRUE.equals(exists);
    }
}
