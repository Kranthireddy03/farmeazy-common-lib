package com.farmeazy.common.audit.crypto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class AuditPayloadEncryptorTest {

    private AuditPayloadEncryptor encryptor;
    private LocalAuditKeyProvider keyProvider;

    @BeforeEach
    void setUp() {
        keyProvider = new LocalAuditKeyProvider();
        ReflectionTestUtils.setField(keyProvider, "currentVersion", "v1");
        keyProvider.init(); // Sets up a 32-byte array of zeroes for 'v1'
        encryptor = new AuditPayloadEncryptor(keyProvider);
    }

    @Test
    void testEncryptDecryptOriginalPlaintext() {
        String plainText = "{\"secret\": \"data\"}";
        AuditPayloadEncryptor.EncryptedResult result = encryptor.encrypt(plainText);
        
        assertNotNull(result);
        assertNotNull(result.getEncryptedPayload());
        assertEquals("v1", result.getKeyVersion());
        
        String decrypted = encryptor.decrypt(result.getEncryptedPayload(), result.getKeyVersion());
        assertEquals(plainText, decrypted);
    }

    @Test
    void testEncryptTwiceProducesDifferentCiphertext() {
        String plainText = "{\"secret\": \"data\"}";
        AuditPayloadEncryptor.EncryptedResult result1 = encryptor.encrypt(plainText);
        AuditPayloadEncryptor.EncryptedResult result2 = encryptor.encrypt(plainText);
        
        assertNotEquals(result1.getEncryptedPayload(), result2.getEncryptedPayload(), "Different IV should produce different ciphertext");
    }

    @Test
    void testTamperCiphertextFails() {
        String plainText = "{\"secret\": \"data\"}";
        AuditPayloadEncryptor.EncryptedResult result = encryptor.encrypt(plainText);
        
        byte[] cipherBytes = Base64.getDecoder().decode(result.getEncryptedPayload());
        // Flip a bit in the ciphertext (which includes the tag)
        cipherBytes[cipherBytes.length - 1] ^= 1;
        String tampered = Base64.getEncoder().encodeToString(cipherBytes);
        
        assertThrows(RuntimeException.class, () -> encryptor.decrypt(tampered, result.getKeyVersion()), "Tampered ciphertext must fail authentication");
    }

    @Test
    void testWrongKeyFails() {
        String plainText = "{\"secret\": \"data\"}";
        AuditPayloadEncryptor.EncryptedResult result = encryptor.encrypt(plainText);
        
        // Add a wrong key
        LocalAuditKeyProvider wrongKeyProvider = new LocalAuditKeyProvider();
        ReflectionTestUtils.setField(wrongKeyProvider, "currentVersion", "v2");
        byte[] wrongKey = new byte[32];
        wrongKey[0] = 1; // Different key
        ReflectionTestUtils.setField(wrongKeyProvider, "localKeyBase64", Base64.getEncoder().encodeToString(wrongKey));
        wrongKeyProvider.init();
        
        AuditPayloadEncryptor wrongEncryptor = new AuditPayloadEncryptor(wrongKeyProvider);
        
        assertThrows(RuntimeException.class, () -> wrongEncryptor.decrypt(result.getEncryptedPayload(), "v2"), "Decrypting with wrong key must fail");
    }

    @Test
    void testInvalidPayloadFails() {
        assertThrows(RuntimeException.class, () -> encryptor.decrypt("not-base-64-!!", "v1"));
        
        // Too short (less than IV)
        String shortPayload = Base64.getEncoder().encodeToString(new byte[5]);
        assertThrows(RuntimeException.class, () -> encryptor.decrypt(shortPayload, "v1"));
    }
}
