package com.carcat.webhook.security;

import com.carcat.webhook.config.WebhookProperties;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

@Component
public class WebhookSignatureValidator {

    private static final String HMAC_SHA256 = "HmacSHA256";

    private final WebhookProperties properties;

    public WebhookSignatureValidator(WebhookProperties properties) {
        this.properties = properties;
    }

    public void validate(String providedSignature, String payload) {
        if (providedSignature == null || providedSignature.isBlank()) {
            throw new InvalidSignatureException();
        }

        String expected = computeSignature(payload == null ? "" : payload);
        String normalizedProvided = normalizeSignature(providedSignature);

        if (!MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                normalizedProvided.getBytes(StandardCharsets.UTF_8))) {
            throw new InvalidSignatureException();
        }
    }

    public String computeSignature(String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(
                    properties.getSignature().getSecret().getBytes(StandardCharsets.UTF_8),
                    HMAC_SHA256));
            byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to compute webhook signature", ex);
        }
    }

    private String normalizeSignature(String signature) {
        if (signature.startsWith("sha256=")) {
            return signature.substring("sha256=".length());
        }
        return signature;
    }
}
