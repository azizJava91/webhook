package com.carcat.webhook.security;

public class InvalidSignatureException extends RuntimeException {

    public InvalidSignatureException() {
        super("Invalid X-Signature");
    }
}
