package com.carcat.webhook.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import com.carcat.webhook.config.WebhookSignatureProperties;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class HmacSignatureValidator {

    public static final String HEADER_NAME = "X-Signature";
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final WebhookSignatureProperties signatureProperties;

    public HmacSignatureValidator(WebhookSignatureProperties signatureProperties) {
        this.signatureProperties = signatureProperties;
    }

    public String sign(byte[] payload) {
        String secret = signatureProperties.getSecret();
        if (!StringUtils.hasText(secret)) {
            throw new IllegalStateException("Webhook signature secret is not configured");
        }
        return HexFormat.of().formatHex(computeMac(secret, payload));
    }

    public boolean isValid(HttpServletRequest request, byte[] body) {
        String secret = signatureProperties.getSecret();
        if (!StringUtils.hasText(secret)) {
            return false;
        }
        String provided = request.getHeader(HEADER_NAME);
        if (!StringUtils.hasText(provided)) {
            return false;
        }
        return isValid(secret, resolvePayload(request, body), provided.trim());
    }

    public byte[] resolvePayload(HttpServletRequest request, byte[] body) {
        if (body != null && body.length > 0) {
            return body;
        }
        String queryString = request.getQueryString();
        if (StringUtils.hasText(queryString)) {
            return queryString.getBytes(StandardCharsets.UTF_8);
        }
        return new byte[0];
    }

    private boolean isValid(String secret, byte[] payload, String providedSignature) {
        byte[] expected = computeMac(secret, payload);
        byte[] provided;
        try {
            provided = HexFormat.of().parseHex(providedSignature);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return MessageDigest.isEqual(expected, provided);
    }

    private byte[] computeMac(String secret, byte[] payload) {
        byte[] data = payload != null ? payload : new byte[0];
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            return mac.doFinal(data);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("Failed to compute HMAC signature", e);
        }
    }
}
