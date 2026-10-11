package com.farmeazy.common.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class InternalServiceCredential {
    private InternalServiceCredential() {}
    public static boolean matches(String configured, String supplied) {
        return configured != null && configured.length() >= 32 && supplied != null
                && MessageDigest.isEqual(configured.getBytes(StandardCharsets.UTF_8), supplied.getBytes(StandardCharsets.UTF_8));
    }
}
