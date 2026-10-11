package com.farmeazy.common.audit.repository;

import com.farmeazy.common.entity.AuditDecryptionPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AuditDecryptionPermissionRepository extends JpaRepository<AuditDecryptionPermission, Long> {
    
    @Query("SELECT p FROM AuditDecryptionPermission p WHERE p.userId = :userId AND p.status = 'ACTIVE' AND p.expiresAt > :now")
    Optional<AuditDecryptionPermission> findActivePermissionForUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}
