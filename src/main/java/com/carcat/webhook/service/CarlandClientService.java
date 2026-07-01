package com.carcat.webhook.service;

import com.carcat.webhook.config.CarlandProperties;
import com.carcat.webhook.util.HmacSignatureValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class CarlandClientService {

    private static final String PARTNER_BASE = "/webhook/partner";

    private final RestClient restClient;
    private final CarlandProperties carlandProperties;
    private final HmacSignatureValidator hmacSignatureValidator;

    public String fetchTestResponse() {
        return restClient.get()
                .uri(carlandProperties.getBaseUrl() + PARTNER_BASE + "/test")
                .retrieve()
                .body(String.class);
    }

    public ResponseEntity<Void> findCarByVin(String vin) {
        String queryString = UriComponentsBuilder.newInstance()
                .queryParam("vin", vin.trim())
                .build()
                .encode()
                .getQuery();

        String uri = carlandProperties.getBaseUrl() + PARTNER_BASE + "/car/find?" + queryString;
        String signature = hmacSignatureValidator.sign(queryString.getBytes(StandardCharsets.UTF_8));

        try {
            return restClient.get()
                    .uri(uri)
                    .header(HmacSignatureValidator.HEADER_NAME, signature)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.notFound().build();
        }
    }
}
