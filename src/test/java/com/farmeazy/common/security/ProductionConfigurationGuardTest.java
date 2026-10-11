package com.farmeazy.common.security;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;

class ProductionConfigurationGuardTest {
    private ProductionConfigurationGuard configured() {
        var guard = new ProductionConfigurationGuard();
        ReflectionTestUtils.setField(guard, "databaseUrl", "jdbc:postgresql://database.example/farmeazy");
        ReflectionTestUtils.setField(guard, "databaseUsername", "service-account");
        ReflectionTestUtils.setField(guard, "databasePassword", "test-only-database-password");
        ReflectionTestUtils.setField(guard, "schemaMode", "validate");
        ReflectionTestUtils.setField(guard, "internalSecret", "test-only-internal-credential-at-least-32-characters");
        return guard;
    }

    @Test void acceptsExternalPostgresWithValidatedSchema() {
        assertDoesNotThrow(configured()::validate);
    }

    @Test void rejectsLocalDatabaseAndBlankSecrets() {
        for (String[] invalid : new String[][]{
                {"databaseUrl", "jdbc:h2:mem:unsafe"}, {"databaseUsername", "sa"},
                {"databasePassword", ""}, {"internalSecret", "short"}, {"schemaMode", "update"},
                {"schemaMode", "create-drop"}}) {
            var guard = configured();
            ReflectionTestUtils.setField(guard, invalid[0], invalid[1]);
            assertThrows(IllegalStateException.class, guard::validate, invalid[0]);
        }
    }

    @Test void rejectsFinancialSimulationInProduction() {
        var guard = configured();
        ReflectionTestUtils.setField(guard, "financialSimulation", true);
        assertThrows(IllegalStateException.class, guard::validate);
    }
}
