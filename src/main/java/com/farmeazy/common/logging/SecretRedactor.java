package com.farmeazy.common.logging;

import java.util.regex.Pattern;

public class SecretRedactor {

    private static final String REDACTED_VALUE = "[REDACTED]";
    
    // Pattern to catch typical JSON keys for secrets:
    // "password": "...", "otp": "...", "token": "...", "jwt": "..."
    private static final Pattern JSON_SECRET_PATTERN = Pattern.compile(
            "\"(password|otp|authorization|bearer_token|jwt|refresh_token|session|api_key|client_secret|aws_secret|supabase_key|razorpay_secret|bank_account|card_number|cvv)\"\\s*:\\s*\"[^\"]+\"",
            Pattern.CASE_INSENSITIVE
    );

    // Pattern for Bearer tokens in headers like: Authorization: Bearer eyJ...
    private static final Pattern HEADER_BEARER_PATTERN = Pattern.compile(
            "Bearer\\s+[A-Za-z0-9\\-\\._~\\+/]+=*",
            Pattern.CASE_INSENSITIVE
    );

    // Pattern for basic auth or other direct credentials in query or headers
    private static final Pattern QUERY_SECRET_PATTERN = Pattern.compile(
            "(password|otp|token|key|secret)=[^&\\s]+",
            Pattern.CASE_INSENSITIVE
    );

    public static String redact(String payload) {
        if (payload == null) {
            return null;
        }
        
        String redacted = payload;
        
        // Redact JSON fields
        redacted = JSON_SECRET_PATTERN.matcher(redacted).replaceAll("\"$1\":\"" + REDACTED_VALUE + "\"");
        
        // Redact Header Bearer tokens
        redacted = HEADER_BEARER_PATTERN.matcher(redacted).replaceAll("Bearer " + REDACTED_VALUE);
        
        // Redact query string secrets
        redacted = QUERY_SECRET_PATTERN.matcher(redacted).replaceAll("$1=" + REDACTED_VALUE);
        
        return redacted;
    }
}
