package com.farmeazy.common.audit.repository;

import com.farmeazy.common.entity.DecryptionAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DecryptionAuditLogRepository extends JpaRepository<DecryptionAuditLog, Long> {
    List<DecryptionAuditLog> findByAuditRecordId(String auditRecordId);
}
