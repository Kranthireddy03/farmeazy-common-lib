package com.farmeazy.common.security;
/** Authoritative session lookup implemented by the authentication service. */
public interface SessionStateStore {
    boolean isActive(Long userId, String sessionId);
}
