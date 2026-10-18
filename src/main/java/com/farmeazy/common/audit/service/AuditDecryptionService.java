package com.farmeazy.common.audit.service;

import com.farmeazy.common.audit.crypto.AuditPayloadEncryptor;
import com.farmeazy.common.audit.repository.ApiAuditTraceRepository;
import com.farmeazy.common.audit.repository.AuditDecryptionPermissionRepository;
import com.farmeazy.common.audit.repository.DecryptionAuditLogRepository;
import com.farmeazy.common.audit.repository.ExternalApiAuditTraceRepository;
import com.farmeazy.common.entity.ApiAuditTrace;
import com.farmeazy.common.entity.ExternalApiAuditTrace;
import com.farmeazy.common.entity.AuditDecryptionPermission;
import com.farmeazy.common.entity.DecryptionAuditLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuditDecryptionService {

    private static final Logger log = LoggerFactory.getLogger(AuditDecryptionService.class);

    private final ApiAuditTraceRepository auditRepository;
    private final ExternalApiAuditTraceRepository externalAuditRepository;
    private final AuditDecryptionPermissionRepository permissionRepository;
    private final DecryptionAuditLogRepository decryptionLogRepository;
    private final AuditPayloadEncryptor encryptor;

    public AuditDecryptionService(
            ApiAuditTraceRepository auditRepository,
            ExternalApiAuditTraceRepository externalAuditRepository,
            AuditDecryptionPermissionRepository permissionRepository,
            DecryptionAuditLogRepository decryptionLogRepository,
            AuditPayloadEncryptor encryptor) {
        this.auditRepository = auditRepository;
        this.externalAuditRepository = externalAuditRepository;
        this.permissionRepository = permissionRepository;
        this.decryptionLogRepository = decryptionLogRepository;
        this.encryptor = encryptor;
    }

    /**
     * Decrypts an audit payload securely.
     */
    @Transactional
    public AuditDecryptionResult decryptPayload(
            String traceId, 
            Long actorUserId, 
            String actorEmail, 
            String reason, 
            String ticketReference, 
            String correlationId,
            String sourceIp,
            String userAgent) {

        log.info("Decryption attempt by {} for traceId {}", actorUserId, traceId);
        
        Optional<AuditDecryptionPermission> activePermission = permissionRepository.findActivePermissionForUser(actorUserId, LocalDateTime.now());
        
        if (activePermission.isEmpty()) {
            recordDecryptionAttempt(traceId, actorUserId, actorEmail, "BOTH", reason, ticketReference, correlationId, false, null, sourceIp, userAgent);
            log.warn("Decryption DENIED for {}: No active AUDIT_PAYLOAD_DECRYPT permission", actorUserId);
            throw new SecurityException("Unauthorized: Missing or expired AUDIT_PAYLOAD_DECRYPT permission");
        }

        ApiAuditTrace trace = auditRepository.findById(traceId)
                .orElseThrow(() -> {
                    recordDecryptionAttempt(traceId, actorUserId, actorEmail, "BOTH", reason, ticketReference, correlationId, false, activePermission.get().getId(), sourceIp, userAgent);
                    return new IllegalArgumentException("Audit trace not found");
                });

        if (trace.getExpiresAt() != null && trace.getExpiresAt().isBefore(LocalDateTime.now())) {
            recordDecryptionAttempt(traceId, actorUserId, actorEmail, "BOTH", reason, ticketReference, correlationId, false, activePermission.get().getId(), sourceIp, userAgent);
            throw new SecurityException("Cannot decrypt expired audit record");
        }

        if (trace.getIsEncrypted() == null || !trace.getIsEncrypted()) {
            recordDecryptionAttempt(traceId, actorUserId, actorEmail, "BOTH", reason, ticketReference, correlationId, false, activePermission.get().getId(), sourceIp, userAgent);
            throw new IllegalStateException("Payload is not encrypted");
        }

        String decryptedRequest = null;
        String decryptedResponse = null;

        try {
            if (trace.getRequestPayload() != null && !trace.getRequestPayload().isBlank()) {
                decryptedRequest = encryptor.decrypt(trace.getRequestPayload(), trace.getKeyVersion());
            }
            if (trace.getResponsePayload() != null && !trace.getResponsePayload().isBlank()) {
                decryptedResponse = encryptor.decrypt(trace.getResponsePayload(), trace.getKeyVersion());
            }
            
            // Generate success audit record
            recordDecryptionAttempt(traceId, actorUserId, actorEmail, "BOTH", reason, ticketReference, correlationId, true, activePermission.get().getId(), sourceIp, userAgent);
            
            return new AuditDecryptionResult(
                trace.getTraceId(),
                trace.getCorrelationId(),
                trace.getServiceName(),
                trace.getEndpoint(),
                trace.getTimestamp().toString(),
                decryptedRequest,
                decryptedResponse
            );
        } catch (Exception e) {
            recordDecryptionAttempt(traceId, actorUserId, actorEmail, "BOTH", reason, ticketReference, correlationId, false, activePermission.get().getId(), sourceIp, userAgent);
            log.error("Failed to decrypt payload for trace {}", traceId, e);
            throw new SecurityException("Failed to decrypt payload. It may have been corrupted or the key version is unavailable.");
        }
    }

    @Transactional
    public AuditDecryptionResult decryptExternalPayload(
            String traceId, 
            Long actorUserId, 
            String actorEmail, 
            String reason, 
            String ticketReference, 
            String correlationId,
            String sourceIp,
            String userAgent) {

        log.info("Decryption attempt (external) by {} for traceId {}", actorUserId, traceId);
        
        Optional<AuditDecryptionPermission> activePermission = permissionRepository.findActivePermissionForUser(actorUserId, LocalDateTime.now());
        
        if (activePermission.isEmpty()) {
            recordDecryptionAttempt(traceId, actorUserId, actorEmail, "BOTH", reason, ticketReference, correlationId, false, null, sourceIp, userAgent);
            log.warn("Decryption DENIED for {}: No active AUDIT_PAYLOAD_DECRYPT permission", actorUserId);
            throw new SecurityException("Unauthorized: Missing or expired AUDIT_PAYLOAD_DECRYPT permission");
        }

        ExternalApiAuditTrace trace = externalAuditRepository.findById(traceId)
                .orElseThrow(() -> {
                    recordDecryptionAttempt(traceId, actorUserId, actorEmail, "BOTH", reason, ticketReference, correlationId, false, activePermission.get().getId(), sourceIp, userAgent);
                    return new IllegalArgumentException("External Audit trace not found");
                });

        if (trace.getExpiresAt() != null && trace.getExpiresAt().isBefore(LocalDateTime.now())) {
            recordDecryptionAttempt(traceId, actorUserId, actorEmail, "BOTH", reason, ticketReference, correlationId, false, activePermission.get().getId(), sourceIp, userAgent);
            throw new SecurityException("Cannot decrypt expired audit record");
        }

        if (trace.getIsEncrypted() == null || !trace.getIsEncrypted()) {
            recordDecryptionAttempt(traceId, actorUserId, actorEmail, "BOTH", reason, ticketReference, correlationId, false, activePermission.get().getId(), sourceIp, userAgent);
            throw new IllegalStateException("Payload is not encrypted");
        }

        String decryptedRequest = null;
        String decryptedResponse = null;

        try {
            if (trace.getRequestPayload() != null && !trace.getRequestPayload().isBlank()) {
                decryptedRequest = encryptor.decrypt(trace.getRequestPayload(), trace.getKeyVersion());
            }
            if (trace.getResponsePayload() != null && !trace.getResponsePayload().isBlank()) {
                decryptedResponse = encryptor.decrypt(trace.getResponsePayload(), trace.getKeyVersion());
            }
            
            recordDecryptionAttempt(traceId, actorUserId, actorEmail, "BOTH", reason, ticketReference, correlationId, true, activePermission.get().getId(), sourceIp, userAgent);
            
            return new AuditDecryptionResult(
                trace.getTraceId(),
                trace.getCorrelationId(),
                trace.getServiceName(),
                trace.getEndpoint(),
                trace.getTimestamp().toString(),
                decryptedRequest,
                decryptedResponse
            );
        } catch (Exception e) {
            recordDecryptionAttempt(traceId, actorUserId, actorEmail, "BOTH", reason, ticketReference, correlationId, false, activePermission.get().getId(), sourceIp, userAgent);
            log.error("Failed to decrypt external payload for trace {}", traceId, e);
            throw new SecurityException("Failed to decrypt payload. It may have been corrupted or the key version is unavailable.");
        }
    }

    private void recordDecryptionAttempt(String traceId, Long actorUserId, String actorEmail, String fieldRequested, 
                                         String reason, String ticketReference, String correlationId, boolean success, 
                                         Long permissionId, String sourceIp, String userAgent) {
        DecryptionAuditLog auditLog = DecryptionAuditLog.builder()
                .actorUserId(actorUserId)
                .actorEmail(actorEmail)
                .auditRecordId(traceId)
                .fieldRequested(fieldRequested)
                .reason(reason)
                .ticketReference(ticketReference)
                .correlationId(correlationId)
                .success(success)
                .permissionId(permissionId)
                .sourceIp(sourceIp)
                .userAgent(userAgent)
                .timestamp(LocalDateTime.now())
                .build();
        decryptionLogRepository.save(auditLog);
    }
}
