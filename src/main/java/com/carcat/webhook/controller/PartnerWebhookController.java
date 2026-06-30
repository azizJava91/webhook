package com.carcat.webhook.controller;

import com.carcat.webhook.dto.VinExistsResponse;
import com.carcat.webhook.dto.WebhookAcceptedResponse;
import com.carcat.webhook.security.WebhookSignatureValidator;
import com.carcat.webhook.service.VinCheckService;
import com.carcat.webhook.service.WebhookEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/webhook/partner")
@RequiredArgsConstructor
public class PartnerWebhookController {

    private final WebhookSignatureValidator signatureValidator;
    private final WebhookEventPublisher eventPublisher;
    private final VinCheckService vinCheckService;

    @PostMapping("/new-service-visit")
    public ResponseEntity<WebhookAcceptedResponse> newServiceVisit(
            @RequestBody String body,
            @RequestHeader("X-Signature") String signature) {
        signatureValidator.validate(signature, body);
        String eventId = eventPublisher.publish(
                "POST",
                "/webhook/partner/new-service-visit",
                null,
                Collections.emptyMap(),
                body);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(WebhookAcceptedResponse.builder().eventId(eventId).status("accepted").build());
    }

    @PutMapping("/edit/visit")
    public ResponseEntity<WebhookAcceptedResponse> editVisit(
            @RequestParam("visitId") Long visitId,
            @RequestBody String body,
            @RequestHeader("X-Signature") String signature) {
        signatureValidator.validate(signature, body);
        String queryString = "visitId=" + visitId;
        String eventId = eventPublisher.publish(
                "PUT",
                "/webhook/partner/edit/visit",
                queryString,
                Collections.emptyMap(),
                body);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(WebhookAcceptedResponse.builder().eventId(eventId).status("accepted").build());
    }

    @GetMapping("/find/car")
    public VinExistsResponse findCar(
            @RequestParam String vin,
            @RequestHeader("X-Signature") String signature) {
        signatureValidator.validate(signature, vin);
        return vinCheckService.checkVinExists(vin);
    }
}
