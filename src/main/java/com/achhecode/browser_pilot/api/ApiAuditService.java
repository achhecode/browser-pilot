
package com.achhecode.browser_pilot.api;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.achhecode.browser_pilot.entity.ApiAuditLog;
import com.achhecode.browser_pilot.repository.ApiAuditLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiAuditService {

    private final ApiAuditLogRepository repository;

    @Async("auditExecutor")
    public void saveAsync(ApiAuditLog auditLog) {
        try {
            repository.saveAndFlush(auditLog);
            log.info("Audit log saved. Request ID: {}",
                    auditLog.getRequestId());
        } catch (Exception e) {
            log.error("Failed to save audit log. Request ID: {}",
                    auditLog.getRequestId(), e);
        }
    }
}
