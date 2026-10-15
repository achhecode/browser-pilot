package com.achhecode.browser_pilot.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table (name = "api_audit_log")
@Getter 
@Setter 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor 
public class ApiAuditLog {

    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false, unique = true)
    private UUID requestId;

    private String traceId;
    private String apiName;
    private String httpMethod;

    @Column(length = 2048)
    private String requestUri;

    @Column(columnDefinition = "TEXT")
    private String queryParams;

    @Column(columnDefinition = "TEXT")
    private String requestBody;

    @Column(columnDefinition = "TEXT")
    private String responseBody;

    private Integer httpStatus;
    private String errorCode;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private String status;
    private Long durationMs;
    private Long requestSizeBytes;
    private Long responseSizeBytes;
    private String userId;
    private String clientIp;
    private String applicationName;
    private String environment;

    private Instant createdAt;
    private Instant completedAt;
}