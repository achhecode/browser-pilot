package com.achhecode.browser_pilot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.achhecode.browser_pilot.entity.ApiAuditLog;

public interface ApiAuditLogRepository
        extends JpaRepository<ApiAuditLog, Long> {
}