package com.farmeazy.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import com.farmeazy.common.dto.UserPrincipal;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:900000}")
    private Long expiration;

    @PostConstruct
    public void validateConfiguration() {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 64
                || secret.startsWith("default-secret") || secret.startsWith("change-me")) {
            throw new IllegalStateException("JWT_SECRET must be an externally supplied secret of at least 64 bytes");
        }
        if (expiration == null || expiration <= 0 || expiration > 900000) {
            throw new IllegalStateException("JWT_EXPIRATION must be between 1 and 900000 milliseconds");
        }
    }

    private SecretKey getSigningKey() {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 64) {
            throw new IllegalStateException("JWT_SECRET must contain at least 64 bytes");
        }
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private void addIdentity(Map<String, Object> claims, UserDetails details) {
        if (!(details instanceof UserPrincipal principal) || principal.getId() == null || principal.getId() <= 0
                || principal.getEmail() == null || principal.getEmail().isBlank()) {
            throw new IllegalArgumentException("A canonical user principal is required");
        }
        if (SuperAdminPolicy.hasSuperAdmin(principal.getRoles()) && !SuperAdminPolicy.isAllowed(principal.getEmail())) {
            log.warn("SUPERADMIN_ASSIGNMENT_ATTEMPT userId={}", principal.getId());
            throw new IllegalArgumentException("Superadmin identity is not allowed");
        }
        if (SuperAdminPolicy.hasSuperAdmin(principal.getRoles()) && !principal.isMfaVerified())
            throw new IllegalArgumentException("Superadmin MFA assurance is required");
        claims.put("userId", principal.getId());
        claims.put("email", principal.getEmail());
        if (principal.isMfaVerified()) claims.put("mfa", true);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(userDetails, (String) null);
    }

    public String generateToken(UserDetails userDetails, String sessionId) {
        Map<String, Object> claims = new HashMap<>();
        addIdentity(claims, userDetails);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put("roles", roles);
        if (sessionId != null) {
            claims.put("sessionId", sessionId);
        }
        return createToken(claims, userDetails.getUsername(), expiration);
    }

    public String generateToken(UserDetails userDetails, long customExpirationMs, String sessionId) {
        Map<String, Object> claims = new HashMap<>();
        addIdentity(claims, userDetails);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put("roles", roles);
        if (sessionId != null) {
            claims.put("sessionId", sessionId);
        }
        return createToken(claims, userDetails.getUsername(), customExpirationMs);
    }

    private String createToken(Map<String, Object> claims, String subject, long expirationMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public UserPrincipal extractPrincipal(String token) {
        Claims claims = extractAllClaims(token);
        Object value = claims.get("userId");
        if (!(value instanceof Number number) || number.longValue() <= 0) {
            throw new JwtException("Canonical identity is missing");
        }
        String email = claims.get("email", String.class);
        if (email == null || email.isBlank() || claims.getSubject() == null) {
            throw new JwtException("Canonical identity is missing");
        }
        List<String> roles = extractRoles(token);
        if (SuperAdminPolicy.hasSuperAdmin(roles) && !SuperAdminPolicy.isAllowed(email)) {
            log.warn("SUPERADMIN_ASSIGNMENT_ATTEMPT userId={}", number.longValue());
            throw new JwtException("Superadmin identity is not allowed");
        }
        boolean mfaVerified = Boolean.TRUE.equals(claims.get("mfa", Boolean.class));
        if (SuperAdminPolicy.hasSuperAdmin(roles) && !mfaVerified) throw new JwtException("Superadmin MFA assurance is required");
        return UserPrincipal.builder().id(number.longValue()).email(email)
                .username(claims.getSubject()).roles(roles).sessionId(claims.get("sessionId", String.class))
                .mfaVerified(mfaVerified).enabled(true).build();
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractSessionId(String token) {
        try {
            return extractClaim(token, claims -> claims.get("sessionId", String.class));
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Object rolesObj = claims.get("roles");
            if (rolesObj instanceof List<?>) {
                List<?> list = (List<?>) rolesObj;
                List<String> roles = new ArrayList<>();
                for (Object item : list) {
                    if (item instanceof Map<?, ?>) {
                        Object auth = ((Map<?, ?>) item).get("authority");
                        if (auth != null) roles.add(auth.toString());
                    } else if (item != null) {
                        roles.add(item.toString());
                    }
                }
                return roles;
            }
            return Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT token");
            return false;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username != null && username.equalsIgnoreCase(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
