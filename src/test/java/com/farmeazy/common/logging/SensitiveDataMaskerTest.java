package com.farmeazy.common.logging;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SensitiveDataMaskerTest {

    @Test
    @DisplayName("Verify maskSecret masks values completely")
    void testMaskSecret() {
        assertEquals("********", SensitiveDataMasker.maskSecret("SuperSecret123!"));
        assertEquals("********", SensitiveDataMasker.maskSecret("eyJhbGciOiJIUzUxMiJ9..."));
        assertNull(SensitiveDataMasker.maskSecret(null));
        assertEquals("", SensitiveDataMasker.maskSecret(""));
    }

    @Test
    @DisplayName("Verify maskEmail partially masks email while preserving domain")
    void testMaskEmail() {
        assertEquals("jo**...**@example.com", SensitiveDataMasker.maskEmail("john.doe@example.com"));
        assertEquals("fa**...**@farm-eazy.com", SensitiveDataMasker.maskEmail("farmer123@farm-eazy.com"));
        assertEquals("********", SensitiveDataMasker.maskEmail("a@b.c"));
    }

    @Test
    @DisplayName("Verify maskPhone partially masks phone number")
    void testMaskPhone() {
        assertEquals("98******10", SensitiveDataMasker.maskPhone("9876543210"));
        assertEquals("********", SensitiveDataMasker.maskPhone("123"));
    }

    @Test
    @DisplayName("Verify maskMessage sanitizes Bearer tokens and key-value secrets in log strings")
    void testMaskMessage() {
        String input = "User logged in with Bearer eyJhbGciOiJIUzUxMiJ9.payload.sig and password=myPassword123 and otp=654321";
        String masked = SensitiveDataMasker.maskMessage(input);

        assertFalse(masked.contains("eyJhbGciOiJIUzUxMiJ9.payload.sig"));
        assertFalse(masked.contains("myPassword123"));
        assertFalse(masked.contains("654321"));
        assertTrue(masked.contains("Bearer ********"));
        assertTrue(masked.contains("password=********"));
        assertTrue(masked.contains("otp=********"));
    }

    @Test
    @DisplayName("Verify isSecretKey identifies sensitive field names")
    void testIsSecretKey() {
        assertTrue(SensitiveDataMasker.isSecretKey("password"));
        assertTrue(SensitiveDataMasker.isSecretKey("jwt_secret"));
        assertTrue(SensitiveDataMasker.isSecretKey("apiKey"));
        assertTrue(SensitiveDataMasker.isSecretKey("razorpay_key_secret"));
        assertTrue(SensitiveDataMasker.isSecretKey("otp"));
        assertFalse(SensitiveDataMasker.isSecretKey("username"));
        assertFalse(SensitiveDataMasker.isSecretKey("productName"));
    }
}
