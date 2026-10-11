package com.farmeazy.common.audit.crypto;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class AuditPayloadEncryptor {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128; // in bits
    
    private final SecureRandom secureRandom = new SecureRandom();
    private final AuditKeyProvider keyProvider;

    public AuditPayloadEncryptor(AuditKeyProvider keyProvider) {
        this.keyProvider = keyProvider;
    }

    /**
     * Encrypts the payload using AES-256-GCM.
     * @param plainText The plaintext payload to encrypt.
     * @return EncryptedResult containing the base64 encoded ciphertext and the key version used.
     */
    public EncryptedResult encrypt(String plainText) {
        if (plainText == null) {
            return null;
        }

        try {
            String keyVersion = keyProvider.getCurrentKeyVersion();
            byte[] key = keyProvider.getKey(keyVersion);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);

            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // Prefix the IV to the ciphertext
            byte[] cipherTextWithIv = new byte[GCM_IV_LENGTH + cipherText.length];
            System.arraycopy(iv, 0, cipherTextWithIv, 0, GCM_IV_LENGTH);
            System.arraycopy(cipherText, 0, cipherTextWithIv, GCM_IV_LENGTH, cipherText.length);

            String base64Encrypted = Base64.getEncoder().encodeToString(cipherTextWithIv);

            return new EncryptedResult(base64Encrypted, keyVersion);

        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt audit payload", e);
        }
    }

    /**
     * Decrypts the payload using AES-256-GCM.
     * @param base64Encrypted The base64 encoded payload (IV + ciphertext + tag).
     * @param keyVersion The version of the key used to encrypt.
     * @return The plaintext string.
     */
    public String decrypt(String base64Encrypted, String keyVersion) {
        if (base64Encrypted == null || keyVersion == null) {
            return null;
        }

        try {
            byte[] key = keyProvider.getKey(keyVersion);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

            byte[] cipherTextWithIv = Base64.getDecoder().decode(base64Encrypted);
            
            if (cipherTextWithIv.length < GCM_IV_LENGTH) {
                throw new IllegalArgumentException("Encrypted payload too short to contain IV");
            }

            byte[] iv = new byte[GCM_IV_LENGTH];
            System.arraycopy(cipherTextWithIv, 0, iv, 0, GCM_IV_LENGTH);

            byte[] cipherText = new byte[cipherTextWithIv.length - GCM_IV_LENGTH];
            System.arraycopy(cipherTextWithIv, GCM_IV_LENGTH, cipherText, 0, cipherText.length);

            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);

            byte[] plainText = cipher.doFinal(cipherText);

            return new String(plainText, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt audit payload", e);
        }
    }

    public static class EncryptedResult {
        private final String encryptedPayload;
        private final String keyVersion;

        public EncryptedResult(String encryptedPayload, String keyVersion) {
            this.encryptedPayload = encryptedPayload;
            this.keyVersion = keyVersion;
        }

        public String getEncryptedPayload() { return encryptedPayload; }
        public String getKeyVersion() { return keyVersion; }
    }
}
