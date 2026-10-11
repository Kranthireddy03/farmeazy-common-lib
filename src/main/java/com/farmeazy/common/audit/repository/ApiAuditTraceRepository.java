package com.farmeazy.common.audit.repository;

import com.farmeazy.common.entity.ApiAuditTrace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ApiAuditTraceRepository extends JpaRepository<ApiAuditTrace, String>, JpaSpecificationExecutor<ApiAuditTrace> {
}
