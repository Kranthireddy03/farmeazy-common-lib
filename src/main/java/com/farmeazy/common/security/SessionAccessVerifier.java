package com.farmeazy.common.security;

import com.farmeazy.common.dto.UserPrincipal;
import com.farmeazy.common.exception.UnauthorizedException;
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
    private final ObjectProvider<SessionStateStore> local;
    private final RestTemplate client;
    private final String url;
    private final String secret;
    public SessionAccessVerifier(ObjectProvider<SessionStateStore> local,
            @Value("${app.services.auth-service-url:http://localhost:8081}") String url,
            @Value("${app.internal-service-key:${INTERNAL_SERVICE_SECRET:}}") String secret) {
        this.local = local; this.url = url; this.secret = secret;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000); factory.setReadTimeout(2000); client = new RestTemplate(factory);
    }
    public void requireActive(UserPrincipal principal) {
        if (principal == null || principal.getId() == null || principal.getSessionId() == null)
            throw new UnauthorizedException("Session is missing");
        boolean active = false;
        try {
            SessionStateStore store = local.getIfAvailable();
            if (store != null) active = store.isActive(principal.getId(), principal.getSessionId());
            else if (secret.length() >= 32) {
                HttpHeaders headers = new HttpHeaders(); headers.set("X-Internal-Service-Key", secret);
                headers.setContentType(MediaType.APPLICATION_JSON);
                var response = client.postForEntity(url + "/api/internal/auth/session-active", new HttpEntity<>(
                        Map.of("userId", principal.getId(), "sessionId", principal.getSessionId()), headers), Boolean.class);
                active = Boolean.TRUE.equals(response.getBody());
            }
        } catch (RuntimeException unavailable) { active = false; }
        if (!active) throw new UnauthorizedException("Session revoked, expired or unavailable");
    }
}
