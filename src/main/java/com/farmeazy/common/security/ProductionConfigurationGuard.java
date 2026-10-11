package com.farmeazy.common.security;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("production")
public class ProductionConfigurationGuard {
    @Value("${spring.datasource.url}") private String databaseUrl;
    @Value("${spring.datasource.username}") private String databaseUsername;
    @Value("${spring.datasource.password}") private String databasePassword;
    @Value("${spring.jpa.hibernate.ddl-auto}") private String schemaMode;
    @Value("${farmeazy.security.internal.secret}") private String internalSecret;
    @Value("${payment.simulation.enabled:false}") private boolean financialSimulation;

    @PostConstruct
    public void validate() {
        if (databaseUrl == null || !databaseUrl.startsWith("jdbc:postgresql:")
                || databaseUsername == null || databaseUsername.isBlank() || "sa".equals(databaseUsername)
                || databasePassword == null || databasePassword.isBlank()) {
            throw new IllegalStateException("Production requires external PostgreSQL database configuration");
        }
        if (!"validate".equals(schemaMode) || internalSecret == null || internalSecret.length() < 32) {
            throw new IllegalStateException("Production requires validated schema and an external internal service secret of at least 32 characters");
        }
        if (financialSimulation) {
            throw new IllegalStateException("Financial simulation is prohibited in production");
        }
    }
}
