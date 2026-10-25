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
                    log.warn("Invalid or expired JWT token for [{} {}]", request.getMethod(), request.getRequestURI());
                    writeUnauthorizedResponse(response, "Token expired or invalid");
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
                log.warn("Session authentication rejected for [{} {}]: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
                writeUnauthorizedResponse(response, e.getMessage());
                return;
            } catch (io.jsonwebtoken.JwtException e) {
                SecurityContextHolder.clearContext();
                log.warn("JWT parsing/verification failed for [{} {}]: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
                writeUnauthorizedResponse(response, "Token verification failed: " + e.getMessage());
                return;
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                log.warn("Unexpected authentication failure for [{} {}]: {}", request.getMethod(), request.getRequestURI(), e.getMessage());
                writeUnauthorizedResponse(response, "Authentication failed");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void writeUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format("{\"success\":false,\"error\":\"UNAUTHORIZED\",\"message\":\"%s\"}",
                message != null ? message.replace("\"", "\\\"") : "Unauthorized"));
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
