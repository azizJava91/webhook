package com.carcat.webhook.controller;

import com.carcat.webhook.service.CarlandTestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private final CarlandTestService carlandTestService;

    public WebhookController(CarlandTestService carlandTestService) {
        this.carlandTestService = carlandTestService;
    }

    @GetMapping("/test")
    public String test() {
        return carlandTestService.fetchTestResponse();
    }
}
