package com.farmeazy.common.audit.service;

import com.farmeazy.common.audit.crypto.AuditPayloadEncryptor;
import com.farmeazy.common.audit.crypto.LocalAuditKeyProvider;
import com.farmeazy.common.audit.repository.ApiAuditTraceRepository;
import com.farmeazy.common.audit.repository.AuditDecryptionPermissionRepository;
import com.farmeazy.common.audit.repository.DecryptionAuditLogRepository;
import com.farmeazy.common.audit.repository.ExternalApiAuditTraceRepository;
import com.farmeazy.common.audit.repository.DecryptionAuditLogRepository;
import com.farmeazy.common.entity.ApiAuditTrace;
import com.farmeazy.common.entity.ExternalApiAuditTrace;
import com.farmeazy.common.entity.AuditDecryptionPermission;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditDecryptionServiceTest {

    @Mock
    private ApiAuditTraceRepository auditRepository;
    
    @Mock
    private ExternalApiAuditTraceRepository externalAuditRepository;
    
    @Mock
    private AuditDecryptionPermissionRepository permissionRepository;
    
    @Mock
    private DecryptionAuditLogRepository decryptionLogRepository;
    
    private AuditPayloadEncryptor encryptor;
    
    private AuditDecryptionService service;

    @BeforeEach
    void setUp() {
        LocalAuditKeyProvider keyProvider = new LocalAuditKeyProvider();
        ReflectionTestUtils.setField(keyProvider, "currentVersion", "v1");
        keyProvider.init(); // uses mock 32-byte key
        encryptor = new AuditPayloadEncryptor(keyProvider);
        service = new AuditDecryptionService(auditRepository, externalAuditRepository, permissionRepository, decryptionLogRepository, encryptor);
    }

    @Test
    void testDecryptionDeniedWhenNoPermission() {
        when(permissionRepository.findActivePermissionForUser(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        assertThrows(SecurityException.class, () -> 
            service.decryptPayload("trace-1", 1L, "dev@example.com", "Debugging", "SR-1", "corr-1", "127.0.0.1", "curl")
        );
        
        verify(decryptionLogRepository).save(argThat(log -> !log.getSuccess()));
    }

    @Test
    void testDecryptionSuccessfulWithPermission() {
        AuditDecryptionPermission perm = new AuditDecryptionPermission();
        perm.setId(10L);
        when(permissionRepository.findActivePermissionForUser(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Optional.of(perm));

        String plainText = "{\"email\":\"test@example.com\"}";
        AuditPayloadEncryptor.EncryptedResult encrypted = encryptor.encrypt(plainText);

        ApiAuditTrace trace = new ApiAuditTrace();
        trace.setTraceId("trace-1");
        trace.setIsEncrypted(true);
        trace.setKeyVersion(encrypted.getKeyVersion());
        trace.setRequestPayload(encrypted.getEncryptedPayload());
        trace.setExpiresAt(LocalDateTime.now().plusDays(1));
        
        when(auditRepository.findById("trace-1")).thenReturn(Optional.of(trace));

        AuditDecryptionResult result = service.decryptPayload("trace-1", 1L, "dev@example.com", "Debugging", "SR-1", "corr-1", "127.0.0.1", "curl");

        assertEquals(plainText, result.getRequest());
        verify(decryptionLogRepository).save(argThat(log -> log.getSuccess() && log.getPermissionId().equals(10L)));
    }

    @Test
    void testExternalDecryptionSuccessfulWithPermission() {
        AuditDecryptionPermission perm = new AuditDecryptionPermission();
        perm.setId(11L);
        when(permissionRepository.findActivePermissionForUser(eq(2L), any(LocalDateTime.class)))
                .thenReturn(Optional.of(perm));

        String plainText = "{\"status\":\"success\"}";
        AuditPayloadEncryptor.EncryptedResult encrypted = encryptor.encrypt(plainText);

        ExternalApiAuditTrace trace = new ExternalApiAuditTrace();
        trace.setTraceId("ext-trace-1");
        trace.setIsEncrypted(true);
        trace.setKeyVersion(encrypted.getKeyVersion());
        trace.setResponsePayload(encrypted.getEncryptedPayload());
        trace.setExpiresAt(LocalDateTime.now().plusDays(1));
        
        when(externalAuditRepository.findById("ext-trace-1")).thenReturn(Optional.of(trace));

        AuditDecryptionResult result = service.decryptExternalPayload("ext-trace-1", 2L, "dev2@example.com", "Debugging External", "SR-2", "corr-2", "127.0.0.1", "curl");

        assertEquals(plainText, result.getResponse());
        verify(decryptionLogRepository).save(argThat(log -> log.getSuccess() && log.getPermissionId().equals(11L)));
    }
}
