package com.farmeazy.common.audit.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PRODUCTION EXTERNAL KEY PROVIDER.
 * Consumes an externally supplied 256-bit key from the orchestration environment.
 * AWS KMS or Vault can later be implemented by creating another AuditKeyProvider implementation.
 */
@Component
@ConditionalOnProperty(name = "app.audit.encryption.provider", havingValue = "production")
public class ProductionAuditKeyProvider implements AuditKeyProvider {

    private static final Logger log = LoggerFactory.getLogger(ProductionAuditKeyProvider.class);

    @Value("${app.audit.encryption.external.key-base64:}")
    private String externalKeyBase64;
    
    @Value("${app.audit.encryption.external.version:prod-v1}")
    private String currentVersion;
    
    private final Map<String, byte[]> keys = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        if (externalKeyBase64 == null || externalKeyBase64.isBlank()) {
            log.error("CRITICAL SECURITY ERROR: Production External Key is missing or blank!");
            throw new IllegalStateException("Production External Key not configured");
        }
        byte[] key = Base64.getDecoder().decode(externalKeyBase64);
        if (key.length != 32) {
            throw new IllegalStateException("Production External Key must be exactly 256 bits (32 bytes)");
        }
        keys.put(currentVersion, key);
        log.info("ProductionAuditKeyProvider initialized with key version: {}", currentVersion);
    }

    @Override
    public String getCurrentKeyVersion() {
        return currentVersion;
    }

    @Override
    public byte[] getKey(String version) {
        byte[] key = keys.get(version);
        if (key == null) {
            // In a real KMS implementation, this would dynamically fetch the historical key version.
            throw new IllegalArgumentException("Unknown key version in production: " + version);
        }
        return key;
    }
}
