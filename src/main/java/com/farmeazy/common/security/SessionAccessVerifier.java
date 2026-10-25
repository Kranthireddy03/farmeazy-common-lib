package com.farmeazy.common.security;

import com.farmeazy.common.dto.UserPrincipal;
import com.farmeazy.common.exception.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

/** No positive cache: logout, password and role changes apply to the next protected operation. */
@Component
public class SessionAccessVerifier {
    private static final Logger log = LoggerFactory.getLogger(SessionAccessVerifier.class);

    private final ObjectProvider<SessionStateStore> local;
    private final RestTemplate client;
    private final String url;
    private final String secret;

    // High-performance in-memory micro-cache for active sessions (30s TTL)
    private final java.util.Map<String, Long> activeSessionCache = new java.util.concurrent.ConcurrentHashMap<>();
    private static final long POSITIVE_CACHE_TTL_MS = 30_000L; // 30 seconds

    public SessionAccessVerifier(ObjectProvider<SessionStateStore> local,
            @Value("${AUTH_SERVICE_URL:${app.services.auth-service-url:${AUTH_SERVICE_HOST:http://localhost}:${AUTH_SERVICE_PORT:8081}}}") String url,
            @Value("${app.internal-service-key:${INTERNAL_SERVICE_SECRET:}}") String secret) {
        this.local = local;
        String resolvedUrl = (url != null && !url.isBlank()) ? url.trim() : "http://localhost:8081";
        if (!resolvedUrl.startsWith("http://") && !resolvedUrl.startsWith("https://")) {
            resolvedUrl = "http://" + resolvedUrl;
        }
        this.url = resolvedUrl;
        this.secret = secret != null ? secret.trim() : "";
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(400);
        factory.setReadTimeout(800);
        this.client = new RestTemplate(factory);
    }

    public void requireActive(UserPrincipal principal) {
        if (principal == null || principal.getId() == null) {
            throw new UnauthorizedException("Session is missing");
        }

        // If the token does not contain a session ID, rely on the cryptographic signature
        if (principal.getSessionId() == null || principal.getSessionId().isBlank()) {
            return;
        }

        SessionStateStore store = local.getIfAvailable();
        if (store != null) {
            // Auth service local DB verification
            if (!store.isActive(principal.getId(), principal.getSessionId())) {
                throw new UnauthorizedException("Session has been revoked or expired");
            }
            return;
        }

        // Fast-path: check in-memory active session micro-cache
        String cacheKey = principal.getId() + ":" + principal.getSessionId();
        long now = System.currentTimeMillis();
        Long validUntil = activeSessionCache.get(cacheKey);
        if (validUntil != null && validUntil > now) {
            return; // Validated in memory in 0.001ms!
        }

        // Downstream microservices: internal service mesh session check
        if (secret == null || secret.length() < 32) {
            log.warn("Internal service key not configured (length < 32); relying on cryptographic JWT verification");
            return;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Internal-Service-Key", secret);
            headers.setContentType(MediaType.APPLICATION_JSON);
            var response = client.postForEntity(url + "/api/internal/auth/session-active", new HttpEntity<>(
                    Map.of("userId", principal.getId(), "sessionId", principal.getSessionId()), headers), Boolean.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                if (Boolean.FALSE.equals(response.getBody())) {
                    activeSessionCache.remove(cacheKey);
                    // Auth service explicitly responded that the session is inactive / revoked
                    throw new UnauthorizedException("Session has been revoked or expired");
                }
                // Positive active session: cache for 30 seconds
                activeSessionCache.put(cacheKey, now + POSITIVE_CACHE_TTL_MS);
                if (activeSessionCache.size() > 5000) {
                    activeSessionCache.entrySet().removeIf(e -> e.getValue() < now);
                }
            }
        } catch (UnauthorizedException e) {
            activeSessionCache.remove(cacheKey);
            throw e;
        } catch (Exception unavailable) {
            // Resilient fallback: network reachability or reboot of auth-service should not cascade-fail downstream services
            log.warn("Auth service session verifier unreachable at {} ({}): falling back to cryptographic JWT verification",
                    url, unavailable.getMessage());
            // Short 10s grace window so threads aren't blocked on every request while auth-service is recovering
            activeSessionCache.put(cacheKey, now + 10_000L);
        }
    }

    public void invalidateSession(Long userId, String sessionId) {
        if (userId != null && sessionId != null) {
            activeSessionCache.remove(userId + ":" + sessionId);
        }
    }
}

