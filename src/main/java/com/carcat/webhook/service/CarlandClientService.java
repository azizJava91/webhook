package com.carcat.webhook.service;

import com.carcat.webhook.config.CarlandProperties;
import com.carcat.webhook.util.HmacSignatureValidator;
import com.carcat.webhook.util.WebhookHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class CarlandClientService {

    private static final String PARTNER_BASE = "/webhook/partner";
    private static final String QUEUED_RESPONSE = "{\"status\":\"queued\",\"message\":\"Carland unavailable, request queued for delivery\"}";

    private final RestClient restClient;
    private final CarlandProperties carlandProperties;
    private final HmacSignatureValidator hmacSignatureValidator;
    private final VisitQueuePublisher visitQueuePublisher;

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

    public ResponseEntity<String> forwardPostWithQueueFallback(String path, byte[] rawBody) {
        return forwardWithQueueFallback(HttpMethod.POST, path, rawBody);
    }

    public ResponseEntity<String> forwardPutWithQueueFallback(String path, byte[] rawBody) {
        return forwardWithQueueFallback(HttpMethod.PUT, path, rawBody);
    }

    public void forwardFromQueue(HttpMethod method, String path, byte[] rawBody) {
        forward(method, path, rawBody, true);
    }

    private ResponseEntity<String> forwardWithQueueFallback(HttpMethod method, String path, byte[] rawBody) {
        try {
            return forward(method, path, rawBody, false);
        } catch (Exception ex) {
            if (!isCarlandUnavailable(ex)) {
                throw ex;
            }
            visitQueuePublisher.publish(method, path, rawBody);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(QUEUED_RESPONSE);
        }
    }

    private ResponseEntity<String> forward(HttpMethod method, String path, byte[] rawBody, boolean fromRabbitQueue) {
        String uri = carlandProperties.getBaseUrl() + PARTNER_BASE + path;
        String signature = hmacSignatureValidator.sign(rawBody);
        RestClient.RequestBodySpec spec = restClient.method(method)
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HmacSignatureValidator.HEADER_NAME, signature);
        if (fromRabbitQueue) {
            spec = spec.header(WebhookHeaders.DELIVERY_SOURCE, WebhookHeaders.DELIVERY_RABBIT_REPLAY);
        }
        return spec.body(rawBody)
                .exchange((request, response) -> ResponseEntity
                        .status(response.getStatusCode())
                        .body(response.bodyTo(String.class)));
    }

    private boolean isCarlandUnavailable(Throwable throwable) {
        if (throwable instanceof ResourceAccessException) {
            return true;
        }
        if (throwable instanceof HttpServerErrorException serverError) {
            HttpStatus status = HttpStatus.resolve(serverError.getStatusCode().value());
            return status == HttpStatus.BAD_GATEWAY
                    || status == HttpStatus.SERVICE_UNAVAILABLE
                    || status == HttpStatus.GATEWAY_TIMEOUT;
        }
        return throwable.getCause() != null && isCarlandUnavailable(throwable.getCause());
    }
}
