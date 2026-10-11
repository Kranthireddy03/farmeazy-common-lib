package com.farmeazy.common.audit.crypto;

public interface AuditKeyProvider {
    String getCurrentKeyVersion();
    byte[] getKey(String version);
}
