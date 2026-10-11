package com.farmeazy.common.audit.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnProperty(name = "app.audit.encryption.provider", havingValue = "local", matchIfMissing = true)
public class LocalAuditKeyProvider implements AuditKeyProvider {

    @Value("${app.audit.encryption.local.key:MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=}")
    private String localKeyBase64;
    
    @Value("${app.audit.encryption.local.version:v1}")
    private String currentVersion;
    
    private final Map<String, byte[]> keys = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        if (localKeyBase64 != null && !localKeyBase64.isBlank()) {
            keys.put(currentVersion, Base64.getDecoder().decode(localKeyBase64));
        } else {
            // Fallback for tests if not provided
            byte[] mockKey = new byte[32]; // 256 bits
            keys.put(currentVersion, mockKey);
        }
    }

    @Override
    public String getCurrentKeyVersion() {
        return currentVersion;
    }

    @Override
    public byte[] getKey(String version) {
        byte[] key = keys.get(version);
        if (key == null) {
            throw new IllegalArgumentException("Unknown key version: " + version);
        }
        return key;
    }
}
