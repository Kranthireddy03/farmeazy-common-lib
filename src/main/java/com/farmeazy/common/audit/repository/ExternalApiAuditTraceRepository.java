package com.farmeazy.common.audit.repository;

import com.farmeazy.common.entity.ExternalApiAuditTrace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalApiAuditTraceRepository extends JpaRepository<ExternalApiAuditTrace, String>, JpaSpecificationExecutor<ExternalApiAuditTrace> {
}
