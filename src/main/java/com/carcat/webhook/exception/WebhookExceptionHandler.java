package com.carcat.webhook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class WebhookExceptionHandler {

    @ExceptionHandler(CarlandUnavailableException.class)
    public ResponseEntity<Map<String, String>> handleCarlandUnavailable(CarlandUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "status", "unavailable",
                        "message", "Carland service is temporarily unreachable"
                ));
    }
}
