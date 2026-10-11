package com.farmeazy.common.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/** For high-entropy opaque refresh tokens, never for passwords or OTPs. */
public final class TokenDigest {
    private TokenDigest() {}
    public static String sha256(String token) {
        if (token == null || token.isBlank()) throw new IllegalArgumentException("Token required");
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException impossible) { throw new IllegalStateException(impossible); }
    }
}
