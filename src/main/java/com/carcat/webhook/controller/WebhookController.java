package com.carcat.webhook.controller;

import com.carcat.webhook.service.CarlandClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook/partner")
@RequiredArgsConstructor
public class WebhookController {

    private final CarlandClientService carlandClientService;

    @GetMapping("/test")
    public String test() {
        return carlandClientService.fetchTestResponse();
    }

    @GetMapping("/car/find")
    public ResponseEntity<Void> findCarByVin(@RequestParam String vin) {
        return carlandClientService.findCarByVin(vin);
    }

    @PostMapping(value = "/new-service-visit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> newServiceVisit(@RequestBody byte[] rawBody) {
        return carlandClientService.forwardPostWithQueueFallback("/new-service-visit", rawBody);
    }

    @PutMapping(value = "/edit/service-visit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateServiceVisit(@RequestBody byte[] rawBody) {
        return carlandClientService.forwardPutWithQueueFallback("/edit/service-visit", rawBody);
    }
}
