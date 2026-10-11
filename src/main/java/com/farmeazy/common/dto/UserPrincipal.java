package com.farmeazy.common.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.farmeazy.common.security.SuperAdminPolicy;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String password;
    private List<String> roles;
    private boolean enabled;
    private String sessionId;
    private boolean mfaVerified;

    public UserPrincipal() {
        this.enabled = true;
    }

    public UserPrincipal(Long id, String username, String email, String phoneNumber, String password, List<String> roles, boolean enabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.roles = roles;
        this.enabled = enabled;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String username;
        private String email;
        private String phoneNumber;
        private String password;
        private List<String> roles;
        private boolean enabled = true;
        private String sessionId;
        private boolean mfaVerified;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder roles(List<String> roles) { this.roles = roles; return this; }
        public Builder enabled(boolean enabled) { this.enabled = enabled; return this; }
        public Builder sessionId(String sessionId) { this.sessionId = sessionId; return this; }
        public Builder mfaVerified(boolean mfaVerified) { this.mfaVerified = mfaVerified; return this; }

        public UserPrincipal build() {
            UserPrincipal principal = new UserPrincipal(id, username, email, phoneNumber, password, roles, enabled);
            principal.sessionId = sessionId;
            principal.mfaVerified = mfaVerified;
            return principal;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }
        return roles.stream()
                .filter(r -> r != null && !r.isBlank())
                .filter(r -> !SuperAdminPolicy.hasSuperAdmin(List.of(r)) || SuperAdminPolicy.isAllowed(email))
                .map(r -> r.startsWith("ROLE_") ? new SimpleGrantedAuthority(r) : new SimpleGrantedAuthority("ROLE_" + r))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return username != null ? username : email; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return enabled; }

    public Long getId() { return id; }
    public String getSessionId() { return sessionId; }
    public boolean isMfaVerified() { return mfaVerified; }
    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setPassword(String password) { this.password = password; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
