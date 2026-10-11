package com.farmeazy.common.logging;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SecretRedactorTest {

    @Test
    void testJsonRedaction() {
        String payload = "{\"user\":\"admin\",\"password\":\"supersecret123\",\"otp\":\"123456\",\"other\":\"value\"}";
        String redacted = SecretRedactor.redact(payload);
        assertFalse(redacted.contains("supersecret123"));
        assertFalse(redacted.contains("123456"));
        assertTrue(redacted.contains("\"password\":\"[REDACTED]\""));
        assertTrue(redacted.contains("\"otp\":\"[REDACTED]\""));
        assertTrue(redacted.contains("\"other\":\"value\""));
    }

    @Test
    void testHeaderBearerRedaction() {
        String payload = "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiJ9.SECRET_TOKEN_HERE";
        String redacted = SecretRedactor.redact(payload);
        assertFalse(redacted.contains("eyJhb"));
        assertTrue(redacted.contains("Bearer [REDACTED]"));
    }

    @Test
    void testQueryParamRedaction() {
        String payload = "GET /api/v1/something?token=abc123xyz&userId=1";
        String redacted = SecretRedactor.redact(payload);
        assertFalse(redacted.contains("abc123xyz"));
        assertTrue(redacted.contains("token=[REDACTED]"));
        assertTrue(redacted.contains("userId=1"));
    }
}
