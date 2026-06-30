package com.carcat.webhook.service;

import com.carcat.webhook.config.CarlandProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CarlandTestService {

    private final RestClient restClient;
    private final CarlandProperties carlandProperties;

    public CarlandTestService(RestClient restClient, CarlandProperties carlandProperties) {
        this.restClient = restClient;
        this.carlandProperties = carlandProperties;
    }

    public String fetchTestResponse() {
        return restClient.get()
                .uri(carlandProperties.getBaseUrl() + "/webhook/test")
                .retrieve()
                .body(String.class);
    }
}
