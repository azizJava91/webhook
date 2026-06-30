package com.carcat.webhook.controller;

import com.carcat.webhook.service.CarlandClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final CarlandClientService carlandClientService;

    @GetMapping("/test")
    public String test() {
        return carlandClientService.fetchTestResponse();
    }

    @GetMapping("/car/find")
    public boolean findCarByVin(@RequestParam String vin) {
        return carlandClientService.findCarByVin(vin);
    }
}
