package com.farmeazy.common.security;

import com.farmeazy.common.constant.RoleConstants;
import com.farmeazy.common.dto.UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtils jwtUtils;
    @org.springframework.beans.factory.annotation.Autowired
    private SessionAccessVerifier sessions;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String jwt = parseJwt(request);

        if (StringUtils.hasText(jwt)) {
            try {
                if (!jwtUtils.validateToken(jwt)) {
                    boolean expired = jwtUtils.isTokenExpired(jwt);
                    String reason = expired ? "TOKEN_EXPIRED" : "INVALID_SIGNATURE";
                    log.warn("[AUTH_REJECT] Category={} Method={} Path={}", reason, request.getMethod(), request.getRequestURI());
                    writeUnauthorizedResponse(response, expired ? "Token expired" : "Invalid token signature", reason);
                    return;
                }

                UserPrincipal principal = jwtUtils.extractPrincipal(jwt);
                sessions.requireActive(principal);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (com.farmeazy.common.exception.UnauthorizedException e) {
                SecurityContextHolder.clearContext();
                log.warn("[AUTH_REJECT] Category=SESSION_REVOKED_OR_INACTIVE Method={} Path={} Reason={}", request.getMethod(), request.getRequestURI(), e.getMessage());
                writeUnauthorizedResponse(response, e.getMessage(), "SESSION_REVOKED_OR_INACTIVE");
                return;
            } catch (io.jsonwebtoken.JwtException e) {
                SecurityContextHolder.clearContext();
                log.warn("[AUTH_REJECT] Category=TOKEN_MALFORMED Method={} Path={} Reason={}", request.getMethod(), request.getRequestURI(), e.getMessage());
                writeUnauthorizedResponse(response, "Token verification failed: " + e.getMessage(), "TOKEN_MALFORMED");
                return;
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                log.warn("[AUTH_REJECT] Category=AUTH_FAILED Method={} Path={} Reason={}", request.getMethod(), request.getRequestURI(), e.getMessage());
                writeUnauthorizedResponse(response, "Authentication failed", "AUTH_FAILED");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void writeUnauthorizedResponse(HttpServletResponse response, String message, String reason) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("X-Auth-Rejection-Reason", reason);
        response.getWriter().write(String.format("{\"success\":false,\"error\":\"UNAUTHORIZED\",\"reason\":\"%s\",\"message\":\"%s\"}",
                reason, message != null ? message.replace("\"", "\\\"") : "Unauthorized"));
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
