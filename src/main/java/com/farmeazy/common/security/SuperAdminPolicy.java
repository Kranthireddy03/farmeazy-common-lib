package com.farmeazy.common.security;

import java.util.Collection;
import java.util.Locale;
import java.util.Set;

/** This allowlist is an invariant, not a configurable domain suffix. */
public final class SuperAdminPolicy {
    private static final Set<String> EMAILS = Set.of(
            "info@farm-eazy.com", "support@farm-eazy.com", "no-reply@farm-eazy.com");

    private SuperAdminPolicy() {}

    public static boolean isAllowed(String email) {
        return email != null && EMAILS.contains(email.trim().toLowerCase(Locale.ROOT));
    }

    public static boolean hasSuperAdmin(Collection<String> roles) {
        return roles != null && roles.stream().anyMatch(role -> role != null &&
                ("SUPERADMIN".equalsIgnoreCase(role.trim()) || "ROLE_SUPERADMIN".equalsIgnoreCase(role.trim())));
    }
}
